package com.tistory.umbum.github_issue_widget_app.viewmodel

import android.content.Intent
import android.view.View
import com.tistory.umbum.github_issue_widget_app.view.OAuthLoginActivity

class ConfigViewModel {
    fun onLogin(v: View) {
        val intent = Intent(v.context, OAuthLoginActivity::class.java)
        v.context.startActivity(intent)
    }
}