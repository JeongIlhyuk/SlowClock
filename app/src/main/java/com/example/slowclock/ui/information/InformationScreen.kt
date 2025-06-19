package com.example.slowclock.ui.information

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationScreen(url: String,
                      onBack: () -> Unit,
                      modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
        }
    }

    LaunchedEffect(url) {
        webView.loadUrl(url)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text="정보 보기") }
            )
        }
    ) { padding ->
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Preview
@Composable
private fun preview(){
    val navController = rememberNavController()
    InformationScreen(url = "",
        onBack = { navController.popBackStack() }
    )
}