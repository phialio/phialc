package io.phial.phialc;

public class ArrayType extends ContainerType {
    private final BaseType elementType;

    public ArrayType(BaseType elementType) {
        this.elementType = elementType;
    }

    public BaseType getElementType() {
        return this.elementType;
    }

    @Override
    public String toOutputType(String outputLanguage) {
        switch (outputLanguage) {
            case "java":
                return "List<" + this.elementType.toOutputType(outputLanguage) + ">";
            default:
                throw new RuntimeException("unexpected output language " + outputLanguage);
        }
    }

    @Override
    public String getName() {
        return "List";
    }
}
