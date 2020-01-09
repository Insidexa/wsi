package application.pusher

import wsi.router.Context

data class UserConnection(
        val id: String?,
        val isAuth: Boolean,
        val ctx: Context
)

class PusherClients(
        private val delegateClients: MutableList<UserConnection>
): MutableList<UserConnection> by delegateClients {
    fun add(vararg connections: UserConnection) {
        connections.forEach { delegateClients.add(it) }
    }
    fun onlyAuth() = PusherClients(delegateClients.filter { it.isAuth }.toMutableList())
    fun all() = PusherClients(delegateClients.toMutableList())
    fun single(id: String) = delegateClients.find { it.id == id }
    fun multiple(vararg ids: String) = PusherClients(delegateClients.filter { ids.contains(it.id) }.toMutableList())
}
