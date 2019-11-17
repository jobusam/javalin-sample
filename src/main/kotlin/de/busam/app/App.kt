package de.busam.app

import de.busam.app.Auth.accessManager
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.security.SecurityUtil.roles
import org.eclipse.jetty.server.Connector
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory

fun main() {

    val app = Javalin.create().apply {
        config.accessManager(Auth::accessManager)
        config.requestLogger { ctx, timeMs ->
                    println("${ctx.method()} ${ctx.path()} took $timeMs ms")}
        after("/users/*") {
            it.header("WWW-Authenticate", """Basic realm="Access to members site", charset="UTF-8"""")
        }

        config.server{ createSecureServer()}
    }

    //$ curl --cacert jetty-custom-cert.pem  https://localhost:8090/routes
    //  app.enableRouteOverview("/routes")

    app.routes {
        path("users") {
            get(UserController::getAllUserIds, roles(ApiRole.ANYONE))
            post(UserController::createUser, roles(ApiRole.USER_WRITE))
            path(":user-id") {
                get(UserController::getUser, roles(ApiRole.USER_READ))
                patch(UserController::updateUser, roles(ApiRole.USER_WRITE))
                delete(UserController::deleteUser, roles(ApiRole.USER_WRITE))
            }
        }
    }
    app.start(8090)
}

// Configure Jetty to use HTTPS
fun createSecureServer(): Server{
    val server = Server()
    val sslContextFactory = SslContextFactory.Server()
    sslContextFactory.keyStorePath = "resources/serverkeystore"
    sslContextFactory.setKeyStorePassword("jetty-pwd")
    val secCon = ServerConnector(server,sslContextFactory)
    secCon.port = 8090
    server.connectors = arrayOf<Connector>(secCon)
    return server
}
