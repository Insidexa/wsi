
import application.auth.AuthModule
import application.factorial.FactorialModule
import application.pusher.PusherModule
import exposedExt.jsonb
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime
import wsi.WsiApp
import java.util.*

object Events : UUIDTable("aggregates") {
    val name = text("name")
    val payload = jsonb<Any>("payload")
    val version = integer("version")
    var createdAt = datetime("createdAt").default(DateTime.now())
}

class Event(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<Event>(Events, Event::class.java)

    var name by Events.name
    var payload by Events.payload
    var version by Events.version
    var createdAt by Events.createdAt
}

suspend fun main(args: Array<String>) {
    WsiApp(listOf(
            FactorialModule::class,
            AuthModule::class,
            PusherModule::class
    )).run()
}