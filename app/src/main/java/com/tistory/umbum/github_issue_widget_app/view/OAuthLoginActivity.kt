package com.tistory.umbum.github_issue_widget_app.view

import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.tistory.umbum.github_issue_widget_app.CLIENT_ID
import com.tistory.umbum.github_issue_widget_app.DBG_TAG
import com.tistory.umbum.github_issue_widget_app.helper.openCustomTab
import com.tistory.umbum.github_issue_widget_app.viewmodel.OAuthLoginViewModel


/**
 * 원래는 OAuth2, Basic 이런 몇몇가지 선택지를 띄워주는 페이지로 구성할 생각이었는데
 * 어차피 OAuth로 처리하는게 제일 안전하지 않나 싶어서,
 * 그냥 OAuthLoginActivity onCreate하자 마자 CCT로 OAuth 로그인하도록 넘어가게 구성했다.
 */
class OAuthLoginActivity : AppCompatActivity() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private lateinit var mViewModel: OAuthLoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = OAuthLoginViewModel(this.application)

        val uri = Uri.Builder()
                .scheme("https")
                .appendPath("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("redirect_uri", "github-issue-widget://login")
                .appendQueryParameter("scope", "repo")
                .build()

        // request OAuth "code"
        openCustomTab(this, uri)
    }

    override fun onResume() {
        super.onResume()
        val intent = getIntent()
        Log.d(DBG_TAG, "OAuthLoginActivity.onResume: intent is ${intent?.action}")
    }

    /**
     * OAuth 과정은
     * 클라이언트가 github 측으로 redirect_uri를 적어 보내면서 code를 요청하면,
     * github 측은 redirect_uri 쪽으로 code를 담아서 보내주고,
     * 클라이언트측은 이 code를 다시 담아서 github 쪽에 요청을 하면 Token이 발급되는 식이다.
     *
     * 여기서 redirect_uri : github-issue-widget://login 이고
     * 그래서 github측에서 응답을 줄 때 github-issue-widget://login?code=... 형식으로 보내주어서
     * 아래의 함수가 호출된다. (xml에 singleTask가 지정되어 있어 onCreate가 아니라 onNewIntent가 호출됨.)
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent != null && Intent.ACTION_VIEW.equals(intent.action)) {
            intent.data?.getQueryParameter("code")?. let {
                mViewModel.resolveAccessToken(it)
            } ?: run {
                Log.d(DBG_TAG, "OAuthLoginActivity.onNewIntent: intent.data or getQueryParameter('code') is null")
            }
        }
        else {
            Log.d(DBG_TAG, "OAuthLoginActivity.onNewIntent: intent is ${intent?.action}")
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
