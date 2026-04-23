package com.rcb.tickets

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rcb.tickets.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        const val PREFS_NAME = "rcb_prefs"
        const val KEY_TOKEN = "bearer_token"

        fun getToken(context: Context): String? {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_TOKEN, null)
        }

        fun saveToken(context: Context, token: String) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_TOKEN, token).apply()
        }

        fun clearToken(context: Context) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().remove(KEY_TOKEN).apply()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.userAgentString =
                "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Mobile Safari/537.36"

            addJavascriptInterface(TokenBridge(this@LoginActivity), "Android")

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    injectTokenInterceptor(view)
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    return false
                }
            }

            loadUrl("https://shop.royalchallengers.com/auth")
        }
    }

    private fun injectTokenInterceptor(webView: WebView) {
        val js = """
            (function() {
                if (window.__rcbTokenHooked) return;
                window.__rcbTokenHooked = true;

                // Hook fetch
                var origFetch = window.fetch;
                window.fetch = function(url, options) {
                    try {
                        if (options && options.headers) {
                            var h = options.headers;
                            var auth = h['authorization'] || h['Authorization'];
                            if (auth && auth.indexOf('Bearer ') === 0) {
                                Android.onTokenFound(auth.substring(7));
                            }
                        }
                    } catch(e) {}
                    return origFetch.apply(this, arguments);
                };

                // Hook XHR setRequestHeader
                var origSet = XMLHttpRequest.prototype.setRequestHeader;
                XMLHttpRequest.prototype.setRequestHeader = function(header, value) {
                    try {
                        if (header.toLowerCase() === 'authorization' && value.indexOf('Bearer ') === 0) {
                            Android.onTokenFound(value.substring(7));
                        }
                    } catch(e) {}
                    return origSet.apply(this, arguments);
                };
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    inner class TokenBridge(private val activity: Activity) {
        @JavascriptInterface
        fun onTokenFound(token: String) {
            if (token.isBlank()) return
            saveToken(activity, token)
            activity.runOnUiThread {
                Toast.makeText(activity, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}
