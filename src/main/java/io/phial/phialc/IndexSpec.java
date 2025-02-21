package io.phial.phialc;

public class IndexSpec {
    private final FieldSpec[] fields;
    private final boolean hash;
    private final boolean unique;

    public IndexSpec(FieldSpec[] fields, boolean hash, boolean unique) {
        this.fields = fields;
        this.hash = hash;
        this.unique = unique;
    }

    public FieldSpec[] getFields() {
        return this.fields;
    }

    public boolean isHash() {
        return this.hash;
    }

    public boolean isUnique() {
        return this.unique;
    }
}
