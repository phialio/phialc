package io.phial.phialc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhialSpec {
    private String packageName;
    private final List<EntitySpec> entities = new ArrayList<>();

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<EntitySpec> getEntities() {
        return Collections.unmodifiableList(this.entities);
    }

    public void addEntity(EntitySpec entity) {
        this.entities.add(entity);
    }
}
