<#include "common.ftl">
package ${package};

import io.phial.*;

import java.util.*;

final class ${entity.name}UpdateImpl extends ${entity.name}Entity implements ${entity.name}Update {
    private long fieldMarker;
    private ${entity.name} base;

    public ${entity.name}UpdateImpl() {
        this.base = null;
    }

    public ${entity.name}UpdateImpl(${entity.name} base) {
        this.base = base;
    }

    @Override
    public long getId() {
        if (this.base != null && (this.fieldMarker & 1L) == 0) {
            return this.base.getId();
        } else {
            return this.id;
        }
    }
<#list entity.fields as field>

    @Override
    public ${getJavaType(field.type)} ${getJavaGetterName(field)}() {
        return this.base != null && (this.fieldMarker & (1L << ${field?index + 1})) == 0 ? this.base.${getJavaGetterName(field)}() : this.${field.name};
    }

    @Override
    public ${entity.name}Update with${field.name?cap_first}(${getJavaType(field.type)} ${field.name}) {
        this.${field.name} = ${field.name};
        this.fieldMarker |= 1L << ${field?index + 1};
        return this;
    }
</#list>

    @Override
    public Entity merge(Entity base) {
        this.base = (${entity.name}) base;
        return new ${entity.name}Entity(this);
    }

    @Override
    public ${entity.name}Update update() {
        return this;
    }
}
