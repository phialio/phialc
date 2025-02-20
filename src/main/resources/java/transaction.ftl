<#include "common.ftl">
package ${package};

import io.phial.*;
import io.phial.specs.*;

import java.util.*;
import java.util.stream.*;

public class Transaction {
    private static final Phial PHIAL = Phial.getInstance();
    private final io.phial.Transaction trx;

    static {
<#list entities as entity>
        PHIAL.createTable(new EntityTableSpec(
                ${entity.name}.class<#rt>
    <#list entity.indexes as index>
                ,<#lt>
                new EntityTableIndexSpec(${index.unique?c}, new EntityComparator() {
                    @Override
                    public String getKeyString(Entity entity) {
                        return <#rt>
        <#list index.fields as field>
            <#if field?index gt 0>
 + " " +<#rt>
            </#if>
                            "${field.name}:" + ((${entity.name}) entity).${getJavaGetterName(field)}()<#t>
        </#list>
                        ;
                    }

                    @Override
                    public int compare(Entity entity1, Entity entity2) {
                        var e1 = (${entity.name}Entity) entity1;
                        var e2 = (${entity.name}Entity) entity2;
                        int c;
        <#list index.fields as field>
            <#if field.type.container>
                        c = Arrays.compare(e1.${getJavaGetterName(field)}(), e2.${getJavaGetterName(field)}());
            <#else>
                <#switch field.type.name>
                    <#on "string", "date">
                        c = e1.${getJavaGetterName(field)}().compareTo(e2.${getJavaGetterName(field)}());
                    <#on "int8">
                        c = Byte.compare(e1.${getJavaGetterName(field)}(), e2.${getJavaGetterName(field)}());
                    <#on "int16">
                        c = Short.compare(e1.${getJavaGetterName(field)}(), e2.${getJavaGetterName(field)}());
                    <#on "int32">
                        c = Integer.compare(e1.${getJavaGetterName(field)}(), e2.${getJavaGetterName(field)}());
                    <#on "int64">
                        c = Long.compare(e1.${getJavaGetterName(field)}(), e2.${getJavaGetterName(field)}());
                    <#on "float">
                        c = Float.compare(e1.${getJavaGetterName(field)}(), e2.${getJavaGetterName(field)}());
                    <#on "double">
                        c = Double.compare(e1.${getJavaGetterName(field)}(), e2.${getJavaGetterName(field)}());
                </#switch>
            </#if>
                        if (c != 0) {
                            return c;
                        }
        </#list>
                        return 0;
                    }
                })<#rt>
    </#list>
                ));<#lt>
</#list>
    }

    public static Transaction newInstance() {
        return new Transaction(PHIAL.newTransaction());
    }

    public Transaction(io.phial.Transaction trx) {
        this.trx = trx;
    }
<#function keyParams index suffix>
    <#return index.fields?map(field -> getJavaType(field.type) + " " + field.name + suffix)?join(", ")>
</#function>
<#function keyBuild entity index suffix>
    <#return "new " + entity.name + "UpdateImpl()" + index.fields?map(field -> ".with" + field.name?cap_first + "(" + field.name + suffix + ")")?join("")>
</#function>

<#list entities as entity>

    public long getNext${entity.name}Id() {
        return this.trx.getNextId(${entity.name}.class);
    }

    public void createOrUpdate${entity.name}(${entity.name}Update entity) {
        this.trx.createOrUpdateEntities(${entity.name}.class, List.of(entity));
    }

    public void remove${entity.name}ById(long id) {
        this.trx.removeEntitiesById(${entity.name}.class, List.of(id));
    }

    public Stream<${entity.name}> getAll${entity.name}() {
        return this.query${entity.name}(1, null, false, null, false);
    }

    public ${entity.name} get${entity.name}ById(long id) {
        var entity = this.trx.getEntityById(${entity.name}.class, id);
        return entity == null ? null : (${entity.name}) entity;
    }
    <#list entity.indexes as index>
        <#assign keyName = index.fields?map(field -> field.name?cap_first)?join("")>
        <#if index.unique>

    public ${entity.name} get${entity.name}By${keyName}(${keyParams(index, "")}) {
        return this.get${entity.name}ByIndex(${index?index + 2}, ${keyBuild(entity, index, "")});
    }
        </#if>
        <#if !index.hash>
    public Stream<${entity.name}> getAll${entity.name}WithHigher${keyName}(${keyParams(index, "")}) {
        return this.query${entity.name}(${index?index + 2}, ${keyBuild(entity, index, "")}, false, null, false);
    }

    public Stream<${entity.name}> getAll${entity.name}With${keyName}OrHigher(${keyParams(index, "")}) {
        return this.query${entity.name}(${index?index + 2}, ${keyBuild(entity, index, "")}, true, null, false);
    }

    public Stream<${entity.name}> getAll${entity.name}WithLower${keyName}(${keyParams(index, "")}) {
        return this.query${entity.name}(${index?index + 2}, null, false, ${keyBuild(entity, index, "")}, false);
    }

    public Stream<${entity.name}> getAll${entity.name}With${keyName}OrLower(${keyParams(index, "")}) {
        return this.query${entity.name}(${index?index + 2}, null, false, ${keyBuild(entity, index, "")}, true);
    }

    public Stream<${entity.name}> getAll${entity.name}Within${keyName}Range(
            ${keyParams(index, "From")},
            boolean fromInclusive,
            ${keyParams(index, "To")},
            boolean toInclusive) {
        return this.query${entity.name}(
                ${index?index + 2},
                ${keyBuild(entity, index, "From")},
                fromInclusive,
                ${keyBuild(entity, index, "To")},
                toInclusive);
    }
        </#if>
    </#list>

    private ${entity.name} get${entity.name}ByIndex(int indexId, ${entity.name} key) {
        var entity = this.trx.getEntityByIndex(${entity.name}.class, indexId, key);
        return entity == null ? null : (${entity.name}) entity;
    }

    private Stream<${entity.name}> query${entity.name}(int indexId,
                                       ${entity.name} from,
                                       boolean fromInclusive,
                                       ${entity.name} to,
                                       boolean toInclusive) {
        return this.trx.queryEntitiesByIndex(${entity.name}.class, indexId, from, fromInclusive, to, toInclusive)
                .map(entity -> (${entity.name}) entity);
    }
</#list>

    public void commit() throws InterruptedException {
        this.trx.commit();
    }

    public void rollback() {
        this.trx.rollback();
    }
}
