package test.java.integration.classic.runtime

import io.github.afezeria.freedao.classic.runtime.Page
import io.github.afezeria.freedao.classic.runtime.context.DaoHelper
import org.junit.Before
import org.junit.Test
import test.BaseTest
import test.Person
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

/**
 *
 * @author afezeria
 */
class PageQueryTest : BaseTest() {

    val impl by lazy {
        getJavaDaoInstance<PageQueryTestDao>()
    }

    @Before
    fun before() {
        initData(
            Person(1, "a"),
            Person(2, "b"),
            Person(3, "c"),
            Person(4, "d"),
            Person(5, "e"),
            Person(6, "f"),
            Person(7, "g"),
            Person(8, "h"),
        )
    }

    @Test
    fun simple() {
        val page = DaoHelper.page(2, 3) {
            impl.selectByOrderByIdAsc()
        }
        assert(page.total == 8L)
        assert(page.records.size == 3)
    }

    @Test
    fun skipCount() {
        val page = DaoHelper.page(
            Page.of(2, 3)
                .setSkipCount(true)
        ) {
            impl.selectByOrderByIdAsc()
        }
        assert(page.total == null)
        assert(page.records.size == 3)
    }

    @Test
    fun overwriteOriginalSort() {
        val page = DaoHelper.page(
            Page.of(2, 3)
                .setOrderBy("name desc")
        ) {
            impl.selectByOrderByIdAsc()
        }
        assert(page.records.size == 3)
        assertContentEquals(page.records.map { it.name }, listOf("e", "d", "c"))
    }

    @Test
    fun overwriteOriginalLimitAndOffset() {
        val page = DaoHelper.page(
            Page.of(2, 3)
                .setOrderBy("name desc")
        ) {
            impl.selectByXml(0, null, 1, 1)
        }
        assert(page.records.size == 3)
        assertContentEquals(page.records.map { it.name }, listOf("e", "d", "c"))
    }

    @Test
    fun orderByOnly() {
        val page = DaoHelper.page(
            Page.of("name asc")
        ) {
            impl.selectByXml(0, null, 1, 1)
        }
        assert(page.records.size == 1)
        assert(page.records[0].name == "b")
    }

//    @Test
//    fun `skip page sql when count sql return 0`() {
//        var page = spyk(Page.of("name asc"))
////        page.orderBy
////        confirmVerified(page)
//        val supplier = spyk(Supplier<Collection<Person>> {
//            impl.selectByXml(100, null, 1, 1)
//        })
//        page = DaoHelper.page(page, supplier)
////        assert(page.records.size == 1)
////        assert(page.records[0].name == "b")
//        verify(exactly = 1) { page.setRecords(ArrayList()) }
//        verify { supplier.get() } returns null
//    }

    @Test
    fun `failure when page closure has multiple query`() {
        assertFailsWith<IllegalStateException>("the closure of DaoHelper.page can only contain one query") {
            DaoHelper.page(
                Page.of("name asc")
            ) {
                impl.list(null)
                impl.list(null)
            }
        }
    }

    @Test
    fun `failure when page closure contains a non-query method`() {
        assertFailsWith<IllegalArgumentException>("INSERT statement does not support paging") {
            DaoHelper.page(
                Page.of(1, 1)
            ) {
                impl.insert(Person())
                listOf<Person>()
            }
        }
    }

    @Test
    fun `failure when page closure contains a single row query`() {
        assertFailsWith<IllegalArgumentException>("single row query does not support paging") {
            DaoHelper.page(
                Page.of(1, 1)
            ) {
                impl.selectOneById(1)
                listOf<Person>()
            }
        }
    }
}