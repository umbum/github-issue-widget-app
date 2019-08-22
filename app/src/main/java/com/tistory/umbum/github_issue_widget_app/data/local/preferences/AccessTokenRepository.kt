package com.tistory.umbum.github_issue_widget_app.data.local.preferences

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.tistory.umbum.github_issue_widget_app.CLIENT_ID
import com.tistory.umbum.github_issue_widget_app.CLIENT_SECRET
import com.tistory.umbum.github_issue_widget_app.data.model.AccessTokenResponse
import com.tistory.umbum.github_issue_widget_app.data.remote.api.GithubClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccessTokenRepository(private val context: Context) {
    var accessToken: String?
        get() = sharedPref.getString("accessToken", null)
        set(accessToken) = sharedPref.edit().putString("accessToken", accessToken).apply()

    private val sharedPref = context.applicationContext
                                .getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)

    fun updateAccessToken(code: String): LiveData<String?> {
        val accessTokenLiveData = MutableLiveData<String?>()
        GithubClient.client.requestAccessToken(CLIENT_ID, CLIENT_SECRET, code).enqueue(object : Callback<AccessTokenResponse> {
            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                accessTokenLiveData.value = null
                accessToken = null
            }

            override fun onResponse(call: Call<AccessTokenResponse>, response: Response<AccessTokenResponse>) {
                accessTokenLiveData.value = response.body()?.accessToken
                accessToken = response.body()?.accessToken
            }
        })
        return accessTokenLiveData
    }
}