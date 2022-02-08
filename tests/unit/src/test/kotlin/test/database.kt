package test

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Statement
import java.util.*


fun main() {

    val config = HikariConfig().apply {
//        jdbcUrl = "jdbc:postgresql://localhost:5432/test"
//        username = "test"
//        password = "123456"
//        driverClassName = "org.postgresql.Driver"
        jdbcUrl = "jdbc:mysql://localhost:3306/test"
        username = "root"
        password = "123456"
        driverClassName = "com.mysql.cj.jdbc.Driver"
    }
    val ds = HikariDataSource(config)
    val connection = ds.connection
    val stmt = connection.prepareStatement(
        """
            insert into user (name ) values (?)
            -- update user set name = ? where id = ?
        """.trimIndent(), Statement.RETURN_GENERATED_KEYS
    )
//    connection.autoCommit = false
//    (1..10).forEach {
//        stmt.setObject(1, it)
////        stmt.setObject(2, it)
//        stmt.addBatch()
//    }
//    println(Arrays.toString(stmt.executeBatch()))
    stmt.setObject(1,"a")
    stmt.execute()
    println("======================")
    println(stmt.updateCount)
    val resultSet = stmt.generatedKeys
    val metaData = resultSet.metaData
    while (resultSet.next()) {
        println("${resultSet.getObject(1)}")
        println(resultSet.getObject("today"))
    }
    connection.commit()


}
