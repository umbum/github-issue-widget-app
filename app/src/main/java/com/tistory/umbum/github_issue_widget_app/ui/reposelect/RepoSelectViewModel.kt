package com.tistory.umbum.github_issue_widget_app.ui.reposelect

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableArrayList
import android.util.Log
import android.widget.Toast
import com.tistory.umbum.github_issue_widget_app.ALL_ISSUES_ID
import com.tistory.umbum.github_issue_widget_app.ALL_ISSUES_TEXT
import com.tistory.umbum.github_issue_widget_app.data.remote.api.GithubApiClient
import com.tistory.umbum.github_issue_widget_app.data.model.RepoItem
import com.tistory.umbum.github_issue_widget_app.data.local.preferences.AccessTokenRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoSelectViewModel(app: Application) : AndroidViewModel(app) {
    private val TAG = this::class.java.simpleName
    private val accessTokenRepository = AccessTokenRepository(getApplication())
    val repoItems = ObservableArrayList<RepoItem>()

    init {
        requestRepos()
    }

    companion object {
        val allIssues = RepoItem(ALL_ISSUES_ID, ALL_ISSUES_TEXT, ALL_ISSUES_TEXT, false, -1)
    }

    fun requestRepos() {
        val access_token = accessTokenRepository.accessToken
        if (access_token == null) {
            Log.d(TAG, "RepoSelectViewModel.requestRepos: access_token is null")
            Toast.makeText(getApplication(), "You need to sign in.", Toast.LENGTH_LONG).show()
            return
        }
        val token_string = "token ${access_token}"
        Log.d(TAG, "RepoSelectViewModel.requestRepos: ${token_string}")

        GithubApiClient.client.getMyRepos(token_string).enqueue(object : Callback<List<RepoItem>> {
            override fun onFailure(call: Call<List<RepoItem>>, t: Throwable) {
                Log.e(TAG, "onFailure: repoistory request fail", t)
                Toast.makeText(getApplication(), "Request for repository information failed.", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<RepoItem>>, response: Response<List<RepoItem>>) {
                val repos = response.body()?.toMutableList()
                if (repos != null) {
                    repos.add(0, allIssues)
                    repoItems.clear()
                    repoItems.addAll(repos)
                } else {
                    Log.e(TAG, "onResponse: response.body() is null.")
                    Toast.makeText(getApplication(), "Failed to get repository information.π", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}

@Suppress("UNCHECKED_CAST")
class RepoSelectViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RepoSelectViewModel(app) as T
    }
}