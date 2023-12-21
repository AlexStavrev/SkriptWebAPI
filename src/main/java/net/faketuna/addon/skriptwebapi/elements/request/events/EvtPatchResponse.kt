package net.faketuna.addon.skriptwebapi.elements.request.events

import ch.njol.skript.Skript
import ch.njol.skript.lang.Literal
import ch.njol.skript.lang.SkriptEvent
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.registrations.EventValues
import ch.njol.skript.util.Getter
import net.faketuna.addon.skriptwebapi.api.server.connection.HttpConnection
import net.faketuna.addon.skriptwebapi.api.server.events.PatchResponseEvent
import net.faketuna.addon.skriptwebapi.api.server.events.PutResponseEvent
import org.bukkit.event.Event

class EvtPatchResponse: SkriptEvent() {

    companion object{
        init {
            Skript.registerEvent("patch request response received", EvtPatchResponse::class.java, PutResponseEvent::class.java, "[skeb] patch [web] [request] response")
                .description("This event fired when receive the patch web request response.")
                .examples("set {_response} to event-connection\n" +
                        "broadcast {_response}'s response code\n" +
                        "broadcast {_response}'s response body\n" +
                        "broadcast {_response}'s response header \"Date\"")
                .since("0.0.4")

            EventValues.registerEventValue(PatchResponseEvent::class.java, HttpConnection::class.java, object: Getter<HttpConnection, PatchResponseEvent>(){
                override fun get(e: PatchResponseEvent): HttpConnection {
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