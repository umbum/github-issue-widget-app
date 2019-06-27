package com.tistory.umbum.github_issue_widget_app.viewmodel

import android.content.Context
import android.util.Log
import com.tistory.umbum.github_issue_widget_app.CLIENT_ID
import com.tistory.umbum.github_issue_widget_app.CLIENT_SECRET
import com.tistory.umbum.github_issue_widget_app.DBG_TAG
import com.tistory.umbum.github_issue_widget_app.api.GithubClient
import com.tistory.umbum.github_issue_widget_app.repository.AccessTokenRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OAuthLoginViewModel(private val _context: Context) {
    private val context = _context.applicationContext
    private val accessTokenRepository = AccessTokenRepository(context)

    fun resolveAccessToken(code: String) {
        GithubClient.githubApi.requestAccessToken(CLIENT_ID, CLIENT_SECRET, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    accessTokenResponse ->
                    Log.d(DBG_TAG, "|resolveAccessToken| ${accessTokenResponse}")
                    accessTokenRepository.accessToken = accessTokenResponse.access_token
                    // 여기서 위젯 업데이트하고... 창 닫고..
                    // 로그인 완료 되었다는 toast 메시지같은건 여기서 하는게 맞나? 아닌가?
                }, {
                    Log.e(DBG_TAG, "|resolveAccessToken| ${it.message}")
                    it.printStackTrace()
                })
    }
}
