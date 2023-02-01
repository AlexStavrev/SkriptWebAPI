package jp.faketuna.addon.skriptwebapi.elements.request.events

import ch.njol.skript.Skript
import ch.njol.skript.lang.Literal
import ch.njol.skript.lang.SkriptEvent
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.registrations.EventValues
import ch.njol.skript.util.Getter
import jp.faketuna.addon.skriptwebapi.api.server.connection.HttpConnection
import jp.faketuna.addon.skriptwebapi.api.server.events.PostResponseEvent
import org.bukkit.event.Event

class EvtPostResponse: SkriptEvent() {

    companion object{
        init {
            Skript.registerEvent("post request response received", EvtPostResponse::class.java, PostResponseEvent::class.java, "[skeb] post [web] [request] response")
                .description("This event fired when receive the post web request response.")
                .examples("set {_response} to event-connection\n" +
                        "broadcast {_response}'s response code\n" +
                        "broadcast {_response}'s response body\n" +
                        "broadcast {_response}'s response header \"Date\"")
                .since("0.0.4")

            EventValues.registerEventValue(PostResponseEvent::class.java, HttpConnection::class.java, object: Getter<HttpConnection, PostResponseEvent>(){
                override fun get(e: PostResponseEvent): HttpConnection {
                    return e.httpConnection
                }
            }, 0)
        }
    }

    override fun toString(event: Event?, debug: Boolean): String {
        return ""
    }

    override fun init(exprs: Array<out Literal<*>>?, matchedPattern: Int, parser: SkriptParser.ParseResult?): Boolean {
        return true
    }

    override fun check(event: Event?): Boolean {
        return true
    }
}