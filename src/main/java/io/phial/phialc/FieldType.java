package io.phial.phialc;

public interface FieldType {
    static FieldType parseType(String type) throws ParserException {
        if (type.endsWith("[]")) {
            return new ArrayType((BaseType) FieldType.parseType(type.substring(0, type.length() - 2)));
        }
        var baseType = BaseType.PRIMITIVE_TYPES.get(type);
        if (baseType == null) {
            throw new ParserException("invalid type " + type);
        }
        return baseType;
    }

    boolean isPrimitive();

    boolean isContainer();

    String toOutputType(String outputLanguage);

    String getName();
}
