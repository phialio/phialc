package io.phial.phialc;

public class MapType extends ContainerType {
    private final BaseType keyType;
    private final BaseType valueType;

    public MapType(BaseType keyType, BaseType valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public BaseType getKeyType() {
        return this.keyType;
    }

    public BaseType getValueType() {
        return this.valueType;
    }

    @Override
    public String toOutputType(String outputLanguage) {
        switch (outputLanguage) {
            case "java":
                return "Map<" + this.keyType.toOutputType(outputLanguage) + ", "
                        + this.valueType.toOutputType(outputLanguage) + ">";
            default:
                throw new RuntimeException("unexpected output language " + outputLanguage);
        }
    }

    @Override
    public String getName() {
        return "Map";
    }
}
