package com.tistory.umbum.github_issue_widget_app.repository

import android.content.Context
import java.lang.NullPointerException

class AccessTokenRepository(private val context: Context) {
    var accessToken: String?
        get() = context.applicationContext
                .getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
                .getString("access_token", null)
        set(accessToken) {
            accessToken ?: throw NullPointerException()

            context.applicationContext
                    .getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
                    .edit()
                    .putString("access_token", accessToken)
                    .apply()
        }
}