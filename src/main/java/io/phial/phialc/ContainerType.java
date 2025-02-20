package io.phial.phialc;

public abstract class ContainerType implements FieldType {
    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isContainer() {
        return true;
    }
}
