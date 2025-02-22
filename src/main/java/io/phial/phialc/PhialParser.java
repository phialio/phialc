package io.phial.phialc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PhialParser {
    private final PhialSpec phialSpec;
    private EntitySpec entitySpec;
    private final Map<String, FieldSpec> fieldMap = new HashMap<>();
    private boolean indexPart;
    private String fieldIndent;
    private String indexIndent;

    private PhialParser(Path path, PhialSpec phialSpec) throws IOException, ParserException {
        this.phialSpec = phialSpec;
        var lines = Files.readAllLines(path);
        for (int i = 0; i < lines.size(); ++i) {
            var line = lines.get(i);
            int c = line.indexOf('#');
            if (c >= 0) {
                line = line.substring(0, c); // remove comments
            }
            line = line.stripTrailing();
            if (line.isEmpty()) {
                // skip empty lines
                continue;
            }
            try {
                if (Character.isWhitespace(line.charAt(0))) {
                    if (this.entitySpec == null) {
                        var indent = PhialParser.getIndent(line);
                        line = line.substring(indent.length());
                        ParserException e;
                        if (line.startsWith("!")) {
                            e = new ParserException("directives should not start with a space");
                        } else if (Character.isLetter(line.charAt(0))) {
                            e = new ParserException("entities should not start with a space");
                        } else {
                            e = new ParserException("invalid line");
                        }
                        e.setColumnNumber(indent.length());
                        throw e;
                    }
                    if (this.indexPart) {
                        this.parseIndex(line);
                    } else {
                        this.parseField(i, line);
                    }
                } else if (line.startsWith("!")) {
                    if (this.entitySpec != null) {
                        throw new ParserException("directives should be before entities");
                    }
                    this.parseDirective(line);
                } else {
                    line = line.strip();
                    PhialParser.checkName("entity", line);
                    if (!Character.isUpperCase(line.charAt(0))) {
                        throw new ParserException("entity names should start with an upper case letter");
                    }
                    this.entitySpec = new EntitySpec(line);
                    phialSpec.addEntity(this.entitySpec);
                    this.indexPart = false;
                    this.fieldIndent = this.indexIndent = null;
                    this.fieldMap.clear();
                }
            } catch (ParserException e) {
                e.setPath(path);
                e.setLineNumber(i);
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(line, e);
            }
        }
        var entityFields = phialSpec.getEntities().stream()
                .collect(Collectors.toMap(
                        EntitySpec::getName,
                        e -> e.getFields().stream()
                                .map(FieldSpec::getName)
                                .collect(Collectors.toSet())));
    }

    private static String getIndent(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return s.substring(0, i);
            }
        }
        throw new RuntimeException("blank string");
    }

    private static String getToken(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (Character.isWhitespace(s.charAt(i))) {
                return s.substring(0, i);
            }
        }
        return s;
    }

    private static void checkName(String type, String name) throws ParserException {
        if (name.isEmpty()) {
            throw new ParserException(type + " name expected");
        }
        if (!Character.isLetter(name.charAt(0))) {
            throw new ParserException("invalid " + type + " name " + name + ": should start with a letter");
        }
        for (int i = 1; i < name.length(); ++i) {
            var ch = name.charAt(i);
            if (!Character.isLetterOrDigit(name.charAt(i))) {
                var e = new ParserException(
                        "invalid " + type + " name " + name + ": should only contain letters or digits");
                e.setColumnNumber(i);
                throw e;
            }
        }
    }

    private static void checkIndention(String indent, String line) throws ParserException {
        for (int i = 0; i < indent.length(); ++i) {
            if (i >= line.length() || indent.charAt(i) != line.charAt(i)) {
                var e = new ParserException("unaligned indention");
                e.setColumnNumber(i);
                throw e;
            }
        }
    }

    private void parseDirective(String line) throws ParserException {
        line = line.substring(1);
        int offset = 1;
        var directive = PhialParser.getToken(line);
        if (directive.isEmpty()) {
            var e = new ParserException("directive expected");
            e.setColumnNumber(1);
            throw e;
        }
        line = line.substring(directive.length());
        offset += directive.length();
        if (directive.equals("package")) {
            // skip whitespaces
            var spaces = PhialParser.getIndent(line);
            offset += spaces.length();
            line = line.substring(spaces.length());

            for (int i = 0; i < line.length(); ++i) {
                var ch = line.charAt(i);
                if (!Character.isLowerCase(ch) && ch != '.') {
                    var e = new ParserException("invalid package name " + line);
                    e.setColumnNumber(offset);
                    throw e;
                }
            }
            this.phialSpec.setPackageName(line);
        } else {
            var e = new ParserException("invalid directive " + directive);
            e.setColumnNumber(1);
            throw e;
        }
    }

    private void parseField(int lineNumber, String line) throws ParserException {
        if (this.fieldIndent == null) {
            this.fieldIndent = PhialParser.getIndent(line);
        }
        PhialParser.checkIndention(this.fieldIndent, line);
        line = line.substring(this.fieldIndent.length());
        if (Character.isWhitespace(line.charAt(0))) {
            var e = new ParserException("unaligned indention");
            e.setColumnNumber(this.fieldIndent.length());
            throw e;
        }
        if (line.equals("index")) {
            this.indexPart = true;
            return;
        }
        var typeString = PhialParser.getToken(line);
        FieldType type;
        try {
            type = FieldType.parseType(typeString);
        } catch (ParserException e) {
            e.setColumnNumber(this.fieldIndent.length());
            throw e;
        }
        line = line.substring(typeString.length());
        int offset = this.fieldIndent.length() + typeString.length();
        var spaces = PhialParser.getIndent(line);
        if (spaces.isEmpty()) {
            var e = new ParserException("space expected");
            e.setColumnNumber(offset);
            throw e;
        }
        line = line.substring(spaces.length());
        offset += spaces.length();
        var name = PhialParser.getToken(line);
        try {
            PhialParser.checkName("field", name);
        } catch (ParserException e) {
            e.setColumnNumber(offset + e.getColumnNumber());
            throw e;
        }
        offset += name.length();
        line = line.substring(name.length());
        if (!line.isEmpty()) {
            spaces = PhialParser.getIndent(line);
            line = line.substring(spaces.length());
            offset += spaces.length();
            var e = new ParserException("unexpected token " + line);
            e.setColumnNumber(offset);
            throw e;
        }
        var fieldSpec = new FieldSpec(this.entitySpec.getFieldCount(), name, type);
        this.entitySpec.addField(fieldSpec);
        this.fieldMap.put(name, fieldSpec);
    }

    private void parseIndex(String line) throws ParserException {
        if (this.indexIndent == null) {
            PhialParser.checkIndention(this.fieldIndent, line);
            this.indexIndent = PhialParser.getIndent(line);
            if (this.indexIndent.length() == this.fieldIndent.length()) {
                var e = new ParserException("more indention expected");
                e.setColumnNumber(this.fieldIndent.length());
                throw e;
            }
        }
        PhialParser.checkIndention(this.indexIndent, line);
        line = line.substring(this.indexIndent.length());
        if (Character.isWhitespace(line.charAt(0))) {
            var e = new ParserException("unaligned indention");
            e.setColumnNumber(this.indexIndent.length());
            throw e;
        }
        var namesToken = PhialParser.getToken(line);
        line = line.substring(namesToken.length());
        int offset = this.indexIndent.length();
        String[] names = namesToken.split("\\+");
        FieldSpec[] fieldSpecs = new FieldSpec[names.length];
        for (int i = 0; i < names.length; ++i) {
            var name = names[i];
            if (name.isEmpty()) {
                var e = new ParserException("index field name should not be empty");
                e.setColumnNumber(offset);
                throw e;
            }
            fieldSpecs[i] = this.fieldMap.get(name);
            if (fieldSpecs[i] == null) {
                var e = new ParserException("unknown field name " + name);
                e.setColumnNumber(offset);
                throw e;
            }
            offset += name.length() + 1; // 1 stands for +
        }
        --offset;
        boolean unique = false;
        boolean hash = false;
        for (; ; ) {
            if (line.isEmpty()) {
                break;
            }
            var spaces = PhialParser.getIndent(line);
            offset += spaces.length();
            line = line.substring(spaces.length());
            var token = PhialParser.getToken(line);
            if (token.equals("hash")) {
                if (hash) {
                    var e = new ParserException("duplicated keyword hash");
                    e.setColumnNumber(offset);
                    throw e;
                }
                hash = true;
            } else if (token.equals("unique")) {
                if (unique) {
                    var e = new ParserException("duplicated keyword unique");
                    e.setColumnNumber(offset);
                    throw e;
                }
                unique = true;
            } else {
                var e = new ParserException("keyword hash or unique expected");
                e.setColumnNumber(offset);
                throw e;
            }
            offset += token.length();
            line = line.substring(token.length());
        }
        this.entitySpec.addIndex(new IndexSpec(fieldSpecs, hash, unique));
    }

    public static PhialSpec parse(Path path) throws IOException, ParserException {
        PhialSpec result = new PhialSpec();
        new PhialParser(path, result);
        return result;
    }


}
