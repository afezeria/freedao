package test

private val regex = "create table\\W+(\\w+)".toRegex()
private fun getTableNameFromCreateTableStatement(string: String): String {
    return regex.find(string)!!.groupValues[1]
}

fun main(args: Array<String>) {
    println(
        getTableNameFromCreateTableStatement(
            """
    create table "clazz"
    (
        "id"   int,
        "teacher_id" int,
        "name" varchar(200),
        primary key("id","teacher_id")
    
    )
        """
        )
    )
}