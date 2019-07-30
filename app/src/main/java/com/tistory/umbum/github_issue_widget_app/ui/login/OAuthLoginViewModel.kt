package com.tistory.umbum.github_issue_widget_app.ui.login

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.tistory.umbum.github_issue_widget_app.data.local.preferences.AccessTokenRepository

class OAuthLoginViewModel(app: Application): AndroidViewModel(app) {
    private val accessTokenRepository = AccessTokenRepository(getApplication())
    lateinit var accessTokenLiveData: LiveData<String?>

    fun initAccessTokenLiveData(code: String) {
        accessTokenLiveData = accessTokenRepository.resolveAccessToken(code)
    }
}

@Suppress("UNCHECKED_CAST")
class OAuthLoginViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OAuthLoginViewModel(app) as T
    }
}