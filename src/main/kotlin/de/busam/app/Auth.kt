package de.busam.app

import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler

enum class ApiRole : Role { ANYONE, USER_READ, USER_WRITE }

object Auth {
    fun accessManager(handler: Handler, ctx: Context, permittedRoles: Set<Role>) {
        when {
            permittedRoles.contains(ApiRole.ANYONE) -> handler.handle(ctx)
            ctx.userRoles.any { it in permittedRoles } -> handler.handle(ctx)
            ctx.path().contains("routes") -> handler.handle(ctx)
            else -> ctx.status(401).json("Unauthorized")
        }
    }

    // get roles from userRoleMap after extracting username/password from basic-auth header
    private val Context.userRoles: List<ApiRole>
        get() = this.basicAuthCredentials().let { (username, password) ->
            userRoleMap[Pair(username, password)] ?: listOf()
        }

    // FIXME: Don't store passwords in clear text (and in memory).
    // use bcrypt (http://www.mindrot.org/projects/jBCrypt/) or something else
    private val userRoleMap = hashMapOf(
        Pair("alice", "123") to listOf(ApiRole.USER_READ),
        Pair("bob", "abc") to listOf(ApiRole.USER_READ, ApiRole.USER_WRITE)
    )

}