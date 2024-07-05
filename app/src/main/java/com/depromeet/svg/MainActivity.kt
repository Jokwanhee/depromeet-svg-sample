package com.depromeet.svg

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.depromeet.svg.databinding.ActivityMainBinding
import com.jkh.svgsample.getHTMLBody

class MainActivity : AppCompatActivity() {
    companion object {
        const val JAVASCRIPT_OBJ = "javascript_obj"
        const val BASE_URL = "file:///android_asset/web/"
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var pd: ProgressDialog
    private lateinit var binding: ActivityMainBinding

    private var duplicationFlag: String = ""

    private fun initProgressDialog() {
        pd = ProgressDialog(this)
        pd.setCancelable(false)
        pd.isIndeterminate = true
        pd.setTitle("render svg")
        pd.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupButtonActions()
        setupWebLayout()
    }

    private fun setupButtonActions() {
        initProgressDialog()
        binding.btnZoomIn.setOnClickListener { binding.webView.zoomIn() }
        binding.btnZoomOut.setOnClickListener { binding.webView.zoomOut() }

        binding.btnSendGray.setOnClickListener {
            if (duplicationFlag == "gray") {
                duplicatedUpdateToWeb()
                duplicationFlag = ""
            } else{
                updateToWeb("gray")
                duplicationFlag = "gray"
            }
        }
        binding.btnSendRed.setOnClickListener {
            if (duplicationFlag == "red") {
                duplicatedUpdateToWeb()
                duplicationFlag = ""
            } else {
                updateToWeb("red")
                duplicationFlag = "red"
            }
        }
    }

    private fun duplicatedUpdateToWeb() {
        binding.webView.evaluateJavascript(
            "javascript: " +
                    "updateFromAndroid(\"" + "duplicate" + "\")", null
        )
    }

    private fun updateToWeb(color: String) {
        binding.webView.evaluateJavascript(
            "javascript: " +
                    "updateFromAndroid(\"" + "$color" + "\")", null
        )
    }

    override fun onResume() {
        super.onResume()
        callVM()
    }

    private fun callVM() {
        val url = "https://svgshare.com/i/17tZ.svg"
        try {
            viewModel.downloadFileFromServer(url)
                .observe(this, Observer { responseBody ->
                    val svgString = responseBody.string()
                    binding.webView.loadDataWithBaseURL(
                        BASE_URL, getHTMLBody(svgString), "text/html",
                        "UTF-8", null
                    )
                    pd.dismiss()
                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @SuppressLint("JavascriptInterface")
    private fun setupWebLayout() {
        binding.webView.setInitialScale(150)
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.displayZoomControls = false
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.addJavascriptInterface(
            JavaScriptInterface(),
            JAVASCRIPT_OBJ
        )
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                injectJavaScriptFunction()
            }
        }
        binding.webView.webChromeClient = WebChromeClient()
    }

    private fun injectJavaScriptFunction() {
        val textToAndroid = "javascript: window.androidObj.textToAndroid = function(message) { " +
                JAVASCRIPT_OBJ + ".textFromWeb(message) }"
        binding.webView.loadUrl(textToAndroid)
    }


    inner class JavaScriptInterface {
        @SuppressLint("SetTextI18n")
        @JavascriptInterface
        fun textFromWeb(fromWeb: String) {
            runOnUiThread {
                binding.tvStateName.text = fromWeb
            }
            Toast.makeText(this@MainActivity, fromWeb, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        binding.webView.removeJavascriptInterface(JAVASCRIPT_OBJ)
        super.onDestroy()
    }
}