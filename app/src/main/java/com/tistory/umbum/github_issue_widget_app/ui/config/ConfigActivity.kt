package com.tistory.umbum.github_issue_widget_app.ui.config

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.tistory.umbum.github_issue_widget_app.R
import com.tistory.umbum.github_issue_widget_app.ui.login.OAuthLoginActivity

class ConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_config)
        setResult(Activity.RESULT_CANCELED)
        val login_btn = findViewById<Button>(R.id.login_btn)
        login_btn.setOnClickListener {
            val intent = Intent(this, OAuthLoginActivity::class.java)
            startActivity(intent)
        }
    }
}
