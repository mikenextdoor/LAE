package pt.isel

import java.sql.Connection

fun getChannelsNames(connection: Connection): List<String> {
    val stmt = connection.prepareStatement("SELECT * FROM channels")
    val rs = stmt.executeQuery()
    val expected =
        listOf(
            "General",
            "Development",
            "Support",
            "Gaming Chat",
            "Esports Discussion",
        ).iterator()
    return mutableListOf<String>().apply {
        while (rs.next()) {
            add(rs.getString("name"))
        }
    }
}
