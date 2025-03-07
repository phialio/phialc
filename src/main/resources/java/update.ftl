<#include "common.ftl">
package ${package};

import io.phial.*;

import java.util.*;

public interface ${entity.name}Update extends ${entity.name}, EntityUpdate {
    static ${entity.name}Update newInstance() {
        return new ${entity.name}UpdateImpl();
    }

    static ${entity.name}Update newInstance(${entity.name} base) {
        return new ${entity.name}UpdateImpl(base);
    }
<#list entity.fields as field>

    ${entity.name}Update with${field.name?cap_first}(${getJavaType(field.type)} ${field.name});
</#list>
}
