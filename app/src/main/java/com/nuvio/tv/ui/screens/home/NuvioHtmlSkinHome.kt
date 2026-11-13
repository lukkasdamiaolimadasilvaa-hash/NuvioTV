package com.nuvio.tv.ui.screens.home

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.nuvio.tv.domain.model.WatchProgress
import org.json.JSONArray
import org.json.JSONObject

@Composable
internal fun NuvioHtmlSkinHome(
    presentation: ModernHomePresentationState,
    onNavigateToDetail: (String, String, String) -> Unit,
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit,
    onNavigateToFolderDetail: (String, String) -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    val itemsByKey = remember(presentation.rows) {
        presentation.rows.list.flatMap { it.items.list }.associateBy { it.key }
    }
    val latestItems = rememberUpdatedState(itemsByKey)
    val latestOnDetail = rememberUpdatedState(onNavigateToDetail)
    val latestOnContinue = rememberUpdatedState(onContinueWatchingClick)
    val latestOnFolder = rememberUpdatedState(onNavigateToFolderDetail)
    val latestOnRoute = rememberUpdatedState(onNavigateToRoute)
    val payload = remember(presentation.rows) { buildSkinPayload(presentation) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var pageReady by remember { mutableStateOf(false) }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    val bridge = remember {
        NuvioSkinJavascriptBridge(
            openItem = { key, action ->
                mainHandler.post {
                    val item = latestItems.value[key] ?: return@post
                    when (val target = item.payload) {
                        is ModernPayload.Catalog -> {
                            // Catalog cards must enter Nuvio's native details/source flow.
                            latestOnDetail.value(target.itemId, target.itemType, target.addonBaseUrl)
                        }
                        is ModernPayload.ContinueWatching -> {
                            when (val continueItem = target.item) {
                                is ContinueWatchingItem.InProgress -> {
                                    if (action == "play") {
                                        latestOnContinue.value(continueItem)
                                    } else {
                                        latestOnDetail.value(
                                            continueItem.progress.contentId,
                                            continueItem.progress.contentType,
                                            continueItem.progress.addonBaseUrl.orEmpty()
                                        )
                                    }
                                }
                                is ContinueWatchingItem.NextUp -> {
                                    if (action == "play") {
                                        latestOnContinue.value(continueItem)
                                    } else {
                                        latestOnDetail.value(
                                            continueItem.info.contentId,
                                            continueItem.info.contentType,
                                            ""
                                        )
                                    }
                                }
                            }
                        }
                        is ModernPayload.CollectionFolder -> {
                            latestOnFolder.value(target.collectionId, target.folderId)
                        }
                    }
                }
            },
            navigate = { destination ->
                mainHandler.post {
                    val route = when (destination) {
                        "search" -> "search"
                        "discover" -> "discover"
                        "library" -> "library"
                        "addon_manager" -> "addon_manager"
                        else -> null
                    }
                    route?.let(latestOnRoute.value)
                }
            }
        )
    }

    val serializedPayload = remember(payload) { payload.toString() }

    AndroidView(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        factory = { context ->
            WebView(context).apply {
                setBackgroundColor(AndroidColor.BLACK)
                isFocusable = true
                isFocusableInTouchMode = true
                overScrollMode = WebView.OVER_SCROLL_NEVER
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = false
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                settings.allowFileAccess = true
                settings.allowContentAccess = false
                settings.allowFileAccessFromFileURLs = false
                settings.allowUniversalAccessFromFileURLs = false
                webChromeClient = WebChromeClient()
                addJavascriptInterface(bridge, "NuvioBridge")
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String?) {
                        pageReady = true
                        view.requestFocus()
                        view.evaluateJavascript(
                            "window.NuvioSkin&&window.NuvioSkin.setData($serializedPayload)",
                            null
                        )
                    }
                }
                webView = this
                loadUrl("file:///android_asset/nuvio-skin-2019.html")
            }
        },
        update = { view ->
            if (pageReady) {
                view.evaluateJavascript(
                    "window.NuvioSkin&&window.NuvioSkin.setData($serializedPayload)",
                    null
                )
            }
        }
    )

    LaunchedEffect(serializedPayload, pageReady) {
        if (pageReady) {
            webView?.evaluateJavascript(
                "window.NuvioSkin&&window.NuvioSkin.setData($serializedPayload)",
                null
            )
        }
    }

    DisposableEffect(webView) {
        onDispose {
            webView?.apply {
                stopLoading()
                removeJavascriptInterface("NuvioBridge")
                destroy()
            }
            webView = null
            pageReady = false
        }
    }
}

internal class NuvioSkinJavascriptBridge(
    private val openItem: (String, String) -> Unit,
    private val navigate: (String) -> Unit
) {
    @JavascriptInterface
    fun openItem(key: String, action: String) {
        openItem.invoke(key, action)
    }

    @JavascriptInterface
    fun navigate(destination: String) {
        navigate.invoke(destination)
    }
}

private fun buildSkinPayload(presentation: ModernHomePresentationState): JSONObject {
    val rows = JSONArray()
    presentation.rows.list.forEach { row ->
        if (row.isLoading || row.items.list.isEmpty()) return@forEach
        val items = JSONArray()
        row.items.list.forEach { item ->
            val hero = item.heroPreview
            val target = JSONObject()
                .put("key", item.key)
                .put("title", hero.title.ifBlank { item.title })
                .put("poster", item.imageUrl ?: hero.poster ?: JSONObject.NULL)
                .put("backdrop", hero.backdrop ?: JSONObject.NULL)
                .put("syn", hero.description ?: JSONObject.NULL)
                .put("year", hero.yearText ?: JSONObject.NULL)
                .put("rating", hero.ageRatingText ?: JSONObject.NULL)
                .put("dur", hero.runtimeText ?: JSONObject.NULL)
                .put("genres", JSONArray(hero.genres.list))
                .put("type", "")
                .put("progress", 0)
                .put("isContinue", false)

            when (val itemPayload = item.payload) {
                is ModernPayload.Catalog -> {
                    target.put("type", itemPayload.itemType)
                }
                is ModernPayload.ContinueWatching -> {
                    target.put("isContinue", true)
                    when (val continueItem = itemPayload.item) {
                        is ContinueWatchingItem.InProgress -> {
                            val progress: WatchProgress = continueItem.progress
                            target.put("type", progress.contentType)
                            target.put("year", JSONObject.NULL)
                            target.put("progress", (progress.progressPercentage * 100f).toInt())
                            val episode = listOfNotNull(progress.season?.let { "T$it" }, progress.episode?.let { "E$it" })
                            if (episode.isNotEmpty()) target.put("episodeLabel", episode.joinToString(""))
                        }
                        is ContinueWatchingItem.NextUp -> {
                            target.put("type", continueItem.info.contentType)
                            target.put(
                                "episodeLabel",
                                listOfNotNull(
                                    continueItem.info.season.takeIf { it > 0 }?.let { "T$it" },
                                    continueItem.info.episode.takeIf { it > 0 }?.let { "E$it" }
                                ).joinToString("")
                            )
                        }
                    }
                }
                is ModernPayload.CollectionFolder -> {
                    target.put("type", "collection")
                }
            }
            items.put(target)
        }
        rows.put(
            JSONObject()
                .put("title", row.title)
                .put("items", items)
                .put("isContinue", row.key == MODERN_CONTINUE_WATCHING_ROW_KEY)
        )
    }
    return JSONObject().put("rows", rows)
}