package com.tistory.umbum.github_issue_widget_app.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableArrayList
import android.util.Log
import android.widget.Toast
import com.tistory.umbum.github_issue_widget_app.ALL_ISSUES_ID
import com.tistory.umbum.github_issue_widget_app.ALL_ISSUES_NAME
import com.tistory.umbum.github_issue_widget_app.DBG_TAG
import com.tistory.umbum.github_issue_widget_app.api.GithubApiClient
import com.tistory.umbum.github_issue_widget_app.model.RepoItem
import com.tistory.umbum.github_issue_widget_app.repository.AccessTokenRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoSelectViewModel(app: Application) : AndroidViewModel(app) {
    private val accessTokenRepository = AccessTokenRepository(getApplication())
    val repoItems = ObservableArrayList<RepoItem>()

    companion object {
        val allIssues = RepoItem(ALL_ISSUES_ID, ALL_ISSUES_NAME, ALL_ISSUES_NAME, false, -1)
    }

    fun requestRepos() {
        val access_token = accessTokenRepository.accessToken
        if (access_token == null) {
            Log.d(DBG_TAG, "RepoSelectActivity: access_token is null")
            Toast.makeText(getApplication(), "You need to sign in.", Toast.LENGTH_LONG).show()
            return
        }
        val token_string = "token ${access_token}"
        Log.d(DBG_TAG, token_string)

        GithubApiClient.client.requestAccountRepos(token_string).enqueue(object : Callback<List<RepoItem>> {
            override fun onFailure(call: Call<List<RepoItem>>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<List<RepoItem>>, response: Response<List<RepoItem>>) {
                val repos = response.body()?.toMutableList()
                if (repos != null) {
                    repos.add(0, allIssues)
                    repoItems.clear()
                    repoItems.addAll(repos)
                    // for (repo in repos.asIterable()) {
                    //     Log.d(DBG_TAG, "${repo.id} ${repo.name} ${repo.private} ${repo.open_issues_count}")
                    // }
                } else {
                    Log.e(DBG_TAG, "requestAccountRepos: response.body() is null.")
                    Toast.makeText(getApplication(), "response.body() is null.", Toast.LENGTH_LONG).show()
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