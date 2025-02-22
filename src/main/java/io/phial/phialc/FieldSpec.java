package io.phial.phialc;

public class FieldSpec {
    private final int id;
    private final String name;
    private final FieldType type;

    public FieldSpec(int id, String name, FieldType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public FieldType getType() {
        return this.type;
    }
}
