<#include "common.ftl">
package ${package};

import io.phial.*;

import java.util.*;

class ${entity.name}Entity extends AbstractEntity implements ${entity.name} {
<#list entity.fields as field>
    protected ${getJavaType(field.type)} ${field.name};
</#list>

    public ${entity.name}Entity() {
<#list entity.fields as field>
        <#if field.type.name == "string">
        this.${field.name} = "";
        <#elseif field.type.container>
        this.${field.name} = Phial.EMPTY_${getJavaType(field.type.elementType)?c_upper_case}_ARRAY;
        </#if>
</#list>
    }

    public ${entity.name}Entity(${entity.name}Entity other) {
        this.id = other.getId();
        this.revision = other.getRevision();
<#list entity.fields as field>
        this.${field.name} = other.${getJavaGetterName(field)}();
</#list>
    }
<#list entity.fields as field>

    @Override
    public ${getJavaType(field.type)} ${getJavaGetterName(field)}() {
        return this.${field.name};
    }
</#list>

    @Override
    public ${entity.name}Update update() {
        return ${entity.name}Update.newInstance(this);
    }
}
