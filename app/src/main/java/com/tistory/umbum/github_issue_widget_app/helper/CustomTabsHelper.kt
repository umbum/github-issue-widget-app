package com.tistory.umbum.github_issue_widget_app.helper
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsClient
import android.content.ComponentName
import com.tistory.umbum.github_issue_widget_app.LoginActivity
import android.support.customtabs.CustomTabsServiceConnection

// Stable = com.android.chrome
// Beta = com.chrome.beta
// Dev = com.chrome.dev
const val CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome"

fun openCustomTab(context: Context, uri: Uri) {
    val connection = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(componentName: ComponentName, client: CustomTabsClient) {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            client.warmup(0L) // This prevents backgrounding after redirection
            customTabsIntent.launchUrl(context, uri)
        }

        override fun onServiceDisconnected(name: ComponentName) {

        }
    }
    CustomTabsClient.bindCustomTabsService(context, CUSTOM_TAB_PACKAGE_NAME, connection)
    // TODO : onDestroy에서 unBind하기.
    // 변수 connection을 액티비티 멤버 변수로 옮길지, 아니면 그냥 bind안하고 launchUrl바로 해버릴지. 그 차이를 먼저 찾아보자.
}

fun openCustomTab(context: Context, url: String) {
    openCustomTab(context, Uri.parse(url))
}