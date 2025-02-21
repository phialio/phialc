package io.phial.phialc;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseType implements FieldType {
    public static final Map<String, BaseType> PRIMITIVE_TYPES = Stream.of(new BaseType("bool", true),
                    new BaseType("string", true),
                    new BaseType("int8", true),
                    new BaseType("int16", true),
                    new BaseType("int32", true),
                    new BaseType("int64", true),
                    new BaseType("float", true),
                    new BaseType("double", true),
                    new BaseType("date", true))
            .collect(Collectors.toMap(t -> t.name, t -> t));

    private final String name;
    private final boolean primitive;

    public BaseType(String name, boolean primitive) {
        this.name = name;
        this.primitive = primitive;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isPrimitive() {
        return this.primitive;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public String toOutputType(String outputLanguage) {
        switch (outputLanguage) {
            case "java":
            default:
                throw new RuntimeException("unexpected output language " + outputLanguage);
        }
    }
}
