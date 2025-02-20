<#include "common.ftl">
package ${package};

import io.phial.*;

import java.util.*;

public interface ${entity.name}Update extends ${entity.name}, EntityUpdate {
    static ${entity.name}Update newInstance() {
        return new ${entity.name}UpdateImpl();
    }

    ${entity.name}Update withId(long id);
<#list entity.fields as field>

    ${entity.name}Update with${field.name?cap_first}(${getJavaType(field.type)} ${field.name});
</#list>
}
