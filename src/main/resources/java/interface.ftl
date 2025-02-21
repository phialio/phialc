<#include "common.ftl">
package ${package};

import io.phial.*;

import java.util.*;

public interface ${entity.name} extends Entity {
<#list entity.fields as field>
    ${getJavaType(field.type)} ${getJavaGetterName(field)}();

</#list>
    ${entity.name}Update update();
}
