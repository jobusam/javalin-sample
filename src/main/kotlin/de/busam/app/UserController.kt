package de.busam.app

import io.javalin.http.Context
import java.util.*

object UserController {

    private data class User(val name: String, val email: String)

    private val users = hashMapOf(
        randomId() to User(name = "Alice", email = "alice@alice.kt"),
        randomId() to User(name = "Bob", email = "bob@bob.kt"),
        randomId() to User(name = "Carol", email = "carol@carol.kt"),
        randomId() to User(name = "Dave", email = "dave@dave.kt")
    )

    // $ curl --cacert jetty-custom-cert.pem https://localhost:8090/users/
    fun getAllUserIds(ctx: Context) {
        ctx.json(users.keys)
    }

    // $ curl -u bob:abc -X POST http://localhost:8090/users/ -d "{\"name\":\"Mia\",\"email\":\"dit@er.de\"}"
    fun createUser(ctx: Context) {
        users[randomId()] = ctx.body<User>()
    }

    // $ curl -u alice:123 http://localhost:8090/users/[id]
    fun getUser(ctx: Context) {
        ctx.json(users[ctx.pathParam(":user-id")]!!)
    }

    // $ curl -u bob:abc -X PATCH http://localhost:8090/users/[id] -d "{\"name\":\"Mia2\",\"email\":\"dit@er.de\"}"
    fun updateUser(ctx: Context) {
        users[ctx.pathParam(":user-id")] = ctx.body<User>()
    }

    // $ curl -u bob:abc -X DELETE http://localhost:8090/users/[id]
    fun deleteUser(ctx: Context) {
        users.remove(ctx.pathParam(":user-id"))
    }

    private fun randomId() = UUID.randomUUID().toString()

}