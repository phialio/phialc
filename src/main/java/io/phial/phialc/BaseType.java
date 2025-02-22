package io.phial.phialc;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseType implements FieldType {
    public static final Map<String, BaseType> PRIMITIVE_TYPES = Stream.of(new BaseType("bool"),
                    new BaseType("string"),
                    new BaseType("int8"),
                    new BaseType("int16"),
                    new BaseType("int32"),
                    new BaseType("int64"),
                    new BaseType("float"),
                    new BaseType("double"))
            .collect(Collectors.toMap(t -> t.name, t -> t));

    private final String name;

    public BaseType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
