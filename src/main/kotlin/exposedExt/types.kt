package exposedExt

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import wsi.transport.Mapper
import java.sql.ResultSet
import kotlin.reflect.KClass

class JSONColumnType(private val kClass: KClass<out Any>) : ColumnType() {
    override var nullable: Boolean = false
    override fun sqlType(): String = "jsonb"

    override fun valueFromDB(value: Any): Any = when(value) {
        is String -> value?.let { Mapper.mapTo(value.toString(), kClass) }
        else -> value
    }

    override fun valueToString(value: Any?) : String = when (value) {
        null -> {
            if (!nullable) error("NULL in non-nullable column")
            "NULL"
        }

        else ->  {
            nonNullValueToString (value)
        }
    }

    override fun valueToDB(value: Any?): Any? = value?.let { notNullValueToDB(it) }

    override fun notNullValueToDB(value: Any): String  = Mapper.toJSON(value)

    override fun nonNullValueToString(value: Any) : String = notNullValueToDB(value)

    override fun readObject(rs: ResultSet, index: Int): Any? = rs.getObject(index)

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        stmt[index] = value
    }
}

inline fun <reified T: Any> Table.jsonb(name: String): Column<T> = registerColumn(name, JSONColumnType(T::class))