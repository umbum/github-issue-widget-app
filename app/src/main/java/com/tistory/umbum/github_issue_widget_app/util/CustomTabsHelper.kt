package com.tistory.umbum.github_issue_widget_app.util
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent


fun openCustomTab(context: Context, uri: Uri) {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(context, uri)
}

fun openCustomTab(context: Context, url: String) {
    openCustomTab(context, Uri.parse(url))
}
