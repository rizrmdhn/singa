package com.singa.asl.ui.screen.web_view

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.gson.Gson
import com.singa.asl.BuildConfig
import com.singa.core.data.source.remote.response.SocialAuthResponse
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun WebViewScreen(
    url: String,
    clearSocialLoginUrl: () -> Unit,
    navigateBack: () -> Unit,
    saveAccessToken: (String) -> Unit,
    saveRefreshToken: (String) -> Unit,
    getAuthUser: () -> Unit,
) {

    BackHandler {
        clearSocialLoginUrl()
        navigateBack()
    }

    WebViewContent(
        url = url,
        onTokenReceived = { token, refreshToken ->
          val launch =  MainScope().launch {
                saveAccessToken(token)
                saveRefreshToken(refreshToken)
                getAuthUser()
            }
            launch.invokeOnCompletion {
                navigateBack()
            }
        },
        clearSocialLoginUrl = clearSocialLoginUrl,
    )
}


@Composable
fun WebViewContent(
    url: String,
    onTokenReceived: (String, String) -> Unit,
    clearSocialLoginUrl: () -> Unit,
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                @SuppressLint("SetJavaScriptEnabled")
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.safeBrowsingEnabled = true
                settings.userAgentString =
                    "Chrome/"

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        handlePageFinished(view, url, onTokenReceived, clearSocialLoginUrl)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        return false
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)

                        // Todo: Implement progress bar
                    }
                }

                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                CookieManager.getInstance().setAcceptCookie(true)
                WebView.setWebContentsDebuggingEnabled(true)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}

@SuppressLint("SetJavaScriptEnabled")
private fun handlePageFinished(
    view: WebView?,
    url: String?,
    onTokenReceived: (String, String) -> Unit,
    clearSocialLoginUrl: () -> Unit,
) {
    if (url != null && url.startsWith(
        if (BuildConfig.PRODUCTION_MODE) {
            BuildConfig.BASE_URL_PROD.plus("login/github/callback")
        } else {
            BuildConfig.BASE_URL.plus("login/github/callback")
        }
    )) {
        // get code and state from url
        view?.evaluateJavascript(
            "(function() { return document.body.innerText; })();"
        ) { response ->
            // remove first " and last " from response
            val cleanedResponse = response.substring(1, response.length - 1).replace("\\", "")
            try {
                val gson = Gson()
                val loginResponse = gson.fromJson(cleanedResponse, SocialAuthResponse::class.java)
                if (loginResponse.meta.code == 200 && loginResponse.meta.status == "success") {
                    val data = loginResponse.data
                    val token = data.token
                    val refreshToken = data.refreshToken
                    clearSocialLoginUrl()
                    onTokenReceived(token, refreshToken)
                    return@evaluateJavascript
                }
            } catch (e: Exception) {
                Log.e("WebViewContent", e.message.toString())
            }
        }
    } else if (url != null && url.startsWith(
        if (BuildConfig.PRODUCTION_MODE) {
            BuildConfig.BASE_URL_PROD.plus("login/google/callback")
        } else {
            BuildConfig.BASE_URL.plus("login/google/callback")
        }
    )) {
        view?.evaluateJavascript(
            "(function() { return document.body.innerText; })();"
        ) { response ->
            val cleanedResponse = response.substring(1, response.length - 1).replace("\\", "")
            try {
                val gson = Gson()
                val loginResponse = gson.fromJson(cleanedResponse, SocialAuthResponse::class.java)
                if (loginResponse.meta.code == 200 && loginResponse.meta.status == "success") {
                    val data = loginResponse.data
                    val token = data.token
                    val refreshToken = data.refreshToken
                    clearSocialLoginUrl()
                    onTokenReceived(token, refreshToken)
                    return@evaluateJavascript
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun WebViewScreenPreview() {
    WebViewScreen(
        url = "https://www.google.com",
        clearSocialLoginUrl = {},
        getAuthUser = {},
        saveRefreshToken = {},
        saveAccessToken = {},
        navigateBack = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WebViewScreenDarkPreview() {
    WebViewScreen(
        url = "https://www.google.com",
        clearSocialLoginUrl = {},
        getAuthUser = {},
        saveRefreshToken = {},
        saveAccessToken = {},
        navigateBack = {}
    )
}