package com.tistory.umbum.github_issue_widget_app.view

import android.appwidget.AppWidgetManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.tistory.umbum.github_issue_widget_app.*
import com.tistory.umbum.github_issue_widget_app.api.GithubClient
import com.tistory.umbum.github_issue_widget_app.model.RepoItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val ALL_ISSUES_ID = 0
const val ALL_ISSUES_NAME = "[All assigned issues]"

class RepoSelectActivity : AppCompatActivity() {

    companion object {
        val allissues = RepoItem(ALL_ISSUES_ID, ALL_ISSUES_NAME, ALL_ISSUES_NAME, false, -1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_select)
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.d(DBG_TAG, "RepoSelectActivity: appWidgetId is INVALID")
            return
        }

        val sharedPreferences = this.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        val access_token = sharedPreferences.getString("access_token", null)
        if (access_token == null) {
            Log.d(DBG_TAG, "RepoSelectActivity: access_token is null")
            Toast.makeText(this, "You need to sign in.", Toast.LENGTH_LONG).show()
            return
        }

        val token_string = "token ${access_token}"
        Log.d(DBG_TAG, token_string)
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val service = retrofit.create(GithubClient.ApiService::class.java)
        val request = service.requestAccountRepos(token_string)
        request.enqueue(object : Callback<List<RepoItem>> {
            override fun onFailure(call: Call<List<RepoItem>>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<List<RepoItem>>, response: Response<List<RepoItem>>) {
                val repos = response.body()?.toMutableList()
                if (repos != null) {
                    repos.add(0, allissues)

                    val adapter = RepoSelectAdapter(this@RepoSelectActivity, repos, appWidgetId)
                    val recyclerView = findViewById<RecyclerView>(R.id.repo_list_view)
                    recyclerView.layoutManager = LinearLayoutManager(this@RepoSelectActivity)
                    recyclerView.adapter = adapter

//                    for (repo in repos.asIterable()) {
//                        Log.d(DBG_TAG, "${repo.id} ${repo.name} ${repo.private} ${repo.open_issues_count}")
//                    }
                } else {
                    Log.d(DBG_TAG, "requestAccountRepos: reponse.body() == null. something wrong")
                }
            }
        })
    }


}
