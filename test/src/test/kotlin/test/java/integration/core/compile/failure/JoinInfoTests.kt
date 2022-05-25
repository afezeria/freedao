package test.java.integration.core.compile.failure

import org.junit.Test
import test.BaseTest
import test.Person

/**
 *
 * @author afezeria
 */
class JoinInfoTests : BaseTest() {
    @Test
    fun missingJoinId() {
        compileFailure<EntityMissingJoinIdBadDao> {
            assertErrorMessageEquals("${EntityMissingJoinId::class.qualifiedName} Join.id cannot be blank")
        }
    }

    @Test
    fun foreignKeyArrayIsEmpty() {
        compileFailure<EntityForeignKeyArrayIsEmptyBadDao> {
            assertErrorMessageEquals("${EntityForeignKeyArrayIsEmpty::class.qualifiedName} Join.foreignKey cannot be empty")
        }
    }

    @Test
    fun foreignKeyPropertyNotExist() {
        compileFailure<EntityForeignKeyPropertyNotExistBadDao> {
            assertErrorMessageEquals("${EntityForeignKeyPropertyNotExist::class.simpleName}: foreign key column does not exist, no property mapped to abc")
        }
    }

    @Test
    fun notSpecifyJoinTable() {
        compileFailure<EntityNotSpecifyJoinTableBadDao> {
            assertErrorMessageEquals("${EntityNotSpecifyJoinTable::class.qualifiedName} Join.Table cannot be blank when Join.entityClass is not specified")
        }
    }

    @Test
    fun notSpecifyReferenceKey() {
        compileFailure<EntityNotSpecifyReferenceKeyBadDao> {
            assertErrorMessageEquals("${EntityNotSpecifyReferenceKey::class.qualifiedName} Join.referenceKey cannot be empty when Join.entityClass is not specified")
        }
    }

    @Test
    fun foreignKeySizeNotEqualReferenceKeySize() {
        compileFailure<EntityForeignKeySizeNotEqualReferenceKeySizeBadDao> {
            assertErrorMessageEquals("${EntityForeignKeySizeNotEqualReferenceKeySize::class.qualifiedName} Join.referenceKey and Join.foreignKey must be the same length")
        }
    }

    @Test
    fun joinEntityNotSpecifyPrimaryKey() {
        compileFailure<EntityJoinEntityNotSpecifyPrimaryKeyBadDao> {
            assertErrorMessageEquals("${NoPrimaryKeyEntity::class.qualifiedName} no primary key specified")
        }
    }

    @Test
    fun joinFieldHaveDifferentType() {
        compileFailure<EntityJoinFieldHaveDifferentTypeBadDao> {
            assertErrorMessageEquals("foreign key field and reference key field have different types. index:0, field: ${EntityJoinFieldHaveDifferentType::class.simpleName}.name:java.lang.String, ${Person::class.simpleName}.id:java.lang.Long")
        }
    }

    @Test
    fun joinIdNotExist() {
        compileFailure<EntityJoinIdNotExistBadDao> {
            assertErrorMessageEquals("EntityJoinIdNotExist.name: joinId:_aa does not exist")
        }
    }

    @Test
    fun referenceValueTypeInconsistent() {
        compileFailure<EntityReferenceValueTypeInconsistentBadDao> {
            assertErrorMessageEquals("inconsistent reference field types: ${EntityReferenceValueTypeInconsistent::class.simpleName}.personAge:java.lang.Long, ${Person::class.simpleName}.name:java.lang.String")
        }
    }

    @Test
    fun joinEntityMissingFieldInReferenceKey() {
        compileFailure<EntityJoinEntityMissingFieldInReferenceKeyBadDao> {
            assertErrorMessageEquals("missing property mapped to column e_id in ${Person::class.qualifiedName}")
        }
    }
}