package jp.faketuna.addon.skriptwebapi.elements.request.effects

import ch.njol.skript.Skript
import ch.njol.skript.doc.Description
import ch.njol.skript.doc.Examples
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import jp.faketuna.addon.skriptwebapi.SkriptWebAPI
import jp.faketuna.addon.skriptwebapi.api.server.events.GetResponseEvent
import jp.faketuna.addon.skriptwebapi.api.server.objects.Header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.event.Event
import java.net.HttpURLConnection
import java.net.URL

@Name("Send a async get web request")
@Description("Send a async get web request with specified header and body.\n" +
        "It calls get response event when connection successful.")
@Examples("set {_header} to blank header\n" +
        "set {_header}'s properties \"Content-Type\" to \"applicaiton/json\"\n" +
        "set {_header}'s properties \"User-Agent\" to \"SkriptWebAPI/0.0.1\"\n" +
        "set {_header}'s properties \"Custom\" to \"Custom header\"\n" +
        "send get request to \"http://domain/\"")
@Since("0.0.3")
class EffGetWebRequest: Effect() {

    companion object{
        init {
            Skript.registerEffect(EffGetWebRequest::class.java,
                "[skeb] send get [web] request to [url] %string% [[(with|and)] header %-header%] [[(with|and)] body %-string%]"
            )
        }
    }

    private var targetURI: Expression<String>? = null
    private var requestHeader: Expression<Header>? = null
    private var requestBody: Expression<String>? = null

    override fun toString(event: Event?, debug: Boolean): String {
        return ""
    }

    override fun init(expr: Array<out Expression<*>>, matchedPattern: Int, isDelayed: Kleenean?, parser: SkriptParser.ParseResult?): Boolean {
        targetURI = expr[0] as Expression<String>
        if (expr[1] != null){
            requestHeader = expr[1] as Expression<Header>
        }
        if (expr[2] != null){
            requestBody = expr[2] as Expression<String>
        }
        return true
    }

    override fun execute(e: Event) {
        val plugin = SkriptWebAPI.Static.getPlugin()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val response = sendRequest(e)
            Bukkit.getScheduler().callSyncMethod(plugin){
                if (response != null) {
                    Bukkit.getPluginManager().callEvent(GetResponseEvent(response))
                } else{
                    plugin.logger.warning("The last web request response is null, May be Header, Body, or Request method is incorrect.")
                }
            }
        })
    }

    private fun sendRequest(e: Event): HttpURLConnection?{
        val uri = targetURI?.getSingle(e)
        val url = URL(uri)
        val header: Header? = requestHeader?.getSingle(e)
        val body: String? = requestBody?.getSingle(e)

        var result: HttpURLConnection? = null
        runBlocking {
            withContext(Dispatchers.IO) {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    connectTimeout = 1000
                    if (header != null) {
                        for (map in header.getHeader()) {
                            setRequestProperty(map.key, map.value.joinToString())
                        }
                    }
                    if (body != null) {
                        doOutput = true
                        outputStream.write(body.toString().toByteArray())
                        outputStream.flush()
                        outputStream.close()

                    }
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        result = this
                    }
                }
            }
        }
        return result
    }
}