package io.phial.phialc;

public interface FieldType {
    static FieldType parseType(String type) throws ParserException {
        var baseType = BaseType.PRIMITIVE_TYPES.get(type);
        if (baseType == null) {
            throw new ParserException("invalid type " + type);
        }
        return baseType;
    }

    String getName();
}
