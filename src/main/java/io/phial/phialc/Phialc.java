package io.phial.phialc;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class Phialc {
    public static void main(String[] args) throws IOException, TemplateException {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(List.of("h", "?"), "show help").forHelp();
        parser.accepts("v", "show version");
        var outputDirSpec = parser.accepts("o", "output directory")
                .withOptionalArg()
                .ofType(File.class)
                .defaultsTo(new File("."));
        var nonOptionsSpec = parser.nonOptions("phial files").ofType(File.class).describedAs("input files");

        OptionSet options = parser.parse(args);
        if (options.has("h")) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }
        if (options.has("v")) {
            System.out.println("Version 1.0");
            System.exit(0);
        }
        var outputDirectory = options.valueOf(outputDirSpec);
        if (!outputDirectory.isDirectory()) {
            System.out.println(outputDirectory.getName() + " is not a directory");
            System.exit(1);
        }
        if (!outputDirectory.canWrite()) {
            System.out.println("can not write to " + outputDirectory.getName());
            System.exit(1);
        }
        String outputLanguage = "java";
        var nonOptionArgs = options.valuesOf(nonOptionsSpec);
        if (nonOptionArgs.isEmpty()) {
            System.out.println("no input file");
            System.exit(1);
        }
        for (var file : nonOptionArgs) {
            if (!file.isFile()) {
                System.out.println(file + " is not a file");
                System.exit(1);
            }
            if (!file.canRead()) {
                System.out.println("can not read " + file);
                System.exit(1);
            }
            if (file.length() > 4 * 1024 * 1024) {
                System.out.println(file + " is larger than 4MB");
                System.exit(1);
            }
            PhialSpec phialSpec = null;
            try {
                phialSpec = PhialParser.parse(file.toPath());
            } catch (ParserException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
            cfg.setClassForTemplateLoading(Phialc.class, "/" + outputLanguage);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
            var root = new HashMap<String, Object>();
            if (outputLanguage.equals("java")) {
                var packageName = phialSpec.getPackageName();
                if (packageName == null) {
                    System.out.println("no package specified in " + file);
                    System.exit(1);
                }
                root.put("package", packageName);
                outputDirectory = new File(outputDirectory, packageName.replace('.', '/'));
                outputDirectory.mkdirs();
            }
            var entities = phialSpec.getEntities();
            root.put("entities", entities);
            var out = new OutputStreamWriter(
                    new FileOutputStream(new File(outputDirectory, "Transaction.java")));
            var tpl = cfg.getTemplate("transaction.ftl");
            tpl.process(root, out);
            out.close();
            for (var entity : entities) {
                root.put("entity", entity);

                // interface
                out = new OutputStreamWriter(
                        new FileOutputStream(new File(outputDirectory, entity.getName() + ".java")));
                tpl = cfg.getTemplate("interface.ftl");
                tpl.process(root, out);
                out.close();

                // entity
                out = new OutputStreamWriter(
                        new FileOutputStream(new File(outputDirectory, entity.getName() + "Entity.java")));
                tpl = cfg.getTemplate("entity.ftl");
                tpl.process(root, out);
                out.close();

                // update
                out = new OutputStreamWriter(
                        new FileOutputStream(new File(outputDirectory, entity.getName() + "Update.java")));
                tpl = cfg.getTemplate("update.ftl");
                tpl.process(root, out);
                out.close();

                // updateImpl
                out = new OutputStreamWriter(
                        new FileOutputStream(new File(outputDirectory, entity.getName() + "UpdateImpl.java")));
                tpl = cfg.getTemplate("update_impl.ftl");
                tpl.process(root, out);
                out.close();
            }
        }
    }
}
