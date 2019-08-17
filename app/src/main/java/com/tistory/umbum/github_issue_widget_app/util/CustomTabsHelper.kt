package com.tistory.umbum.github_issue_widget_app.util
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent


fun openCustomTab(context: Context, uri: Uri, flags: Int? = null) {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    if (flags != null) {
        customTabsIntent.intent.setFlags(flags)
    }
    customTabsIntent.launchUrl(context, uri)
}

fun openCustomTab(context: Context, url: String, flags: Int? = null) {
    openCustomTab(context, Uri.parse(url), flags)
}
