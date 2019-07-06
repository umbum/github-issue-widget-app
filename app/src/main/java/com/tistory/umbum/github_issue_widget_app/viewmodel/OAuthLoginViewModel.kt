package com.tistory.umbum.github_issue_widget_app.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import android.widget.Toast
import com.tistory.umbum.github_issue_widget_app.CLIENT_ID
import com.tistory.umbum.github_issue_widget_app.CLIENT_SECRET
import com.tistory.umbum.github_issue_widget_app.DBG_TAG
import com.tistory.umbum.github_issue_widget_app.api.GithubClient
import com.tistory.umbum.github_issue_widget_app.repository.AccessTokenRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OAuthLoginViewModel(app: Application): AndroidViewModel(app) {
    private val accessTokenRepository = AccessTokenRepository(getApplication())

    fun resolveAccessToken(code: String) {
        GithubClient.client.requestAccessToken(CLIENT_ID, CLIENT_SECRET, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    accessTokenResponse ->
                    Log.d(DBG_TAG, "OAuthLoginViewModel.resolveAccessToken: ${accessTokenResponse}")
                    accessTokenRepository.accessToken = accessTokenResponse.access_token
                    Toast.makeText(getApplication(), "Signed in!", Toast.LENGTH_LONG).show()

                }, {
                    Log.e(DBG_TAG, "OAuthLoginViewModel.resolveAccessToken: ${it.message}")
                    it.printStackTrace()
                    Toast.makeText(getApplication(), "Login Error", Toast.LENGTH_LONG).show()
                })
    }
}
