<#assign javaTypeMap = {
    "string": "String",
    "int8": "byte",
    "int16": "short",
    "int32": "int",
    "int64": "long",
    "bool": "boolean",
    "date": "Date"
}>
<#assign javaElementTypeMap = {
    "string": "String",
    "int8": "Byte",
    "int16": "Short",
    "int32": "Integer",
    "int64": "Long",
    "bool": "Boolean",
    "date": "Date"
}>

<#function getJavaType type>
    <#return javaTypeMap[type.name]>
</#function>

<#function getJavaGetterName field>
    <#return (field.type.name == "bool")?then("is", "get") + field.name?cap_first>
</#function>
