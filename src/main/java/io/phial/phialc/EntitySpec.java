package io.phial.phialc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntitySpec {
    private final String name;
    private final List<FieldSpec> fields = new ArrayList<>();
    private final List<IndexSpec> indexes = new ArrayList<>();

    public EntitySpec(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getFieldCount() {
        return this.fields.size();
    }

    public List<FieldSpec> getFields() {
        return Collections.unmodifiableList(this.fields);
    }

    public void addField(FieldSpec field) {
        this.fields.add(field);
    }

    public List<IndexSpec> getIndexes() {
        return Collections.unmodifiableList(this.indexes);
    }

    public void addIndex(IndexSpec index) {
        this.indexes.add(index);
    }
}
