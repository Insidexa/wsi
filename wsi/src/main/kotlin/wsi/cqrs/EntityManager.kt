package wsi.cqrs

import org.jetbrains.exposed.dao.id.UUIDTable

class EntityManager {
    suspend fun <T: UUIDTable>persist(
            event: T
    ) {
//        suspendedTransactionAsync {
//            event.new(UUID.randomUUID()) {
//                name = event::class.simpleName!!
//                payload = event
//                version = 1
//                createdAt = DateTime.now()
//            }
//        }.await()
    }
}
