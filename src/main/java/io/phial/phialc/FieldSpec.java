package io.phial.phialc;

public class FieldSpec {
    private int id;
    private final String name;
    private final FieldType type;
    private final String link;

    public FieldSpec(int id, String name, FieldType type, String link) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.link = link;
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

    public String getLink() {
        return this.link;
    }
}
