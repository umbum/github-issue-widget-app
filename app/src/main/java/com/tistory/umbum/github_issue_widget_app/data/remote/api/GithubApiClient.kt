package com.tistory.umbum.github_issue_widget_app.data.remote.api

import com.google.gson.JsonArray
import com.tistory.umbum.github_issue_widget_app.data.model.IssueItem
import com.tistory.umbum.github_issue_widget_app.data.model.RepoItem
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

const val GITHUB_API_URL = "https://api.github.com/"

object GithubApiClient {
    val client = Retrofit.Builder()
            .baseUrl(GITHUB_API_URL)
//            .client(OkHttpClient())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Service::class.java)

    interface Service {
        @GET("users/{user}/repos")
        fun requestUserRepos(@Path("user") user: String): Call<JsonArray>

        @GET("/user/repos")
        fun requestAccountRepos(@Header("Authorization") token_string: String): Call<List<RepoItem>>

        @GET("/repos/{user}/{repo}/issues")
        fun requestIssues(@Header("Authorization") token_string: String,
                          @Path("user") user: String,
                          @Path("repo") repo: String): Call<List<IssueItem>>

        @GET("/issues")
        fun requestAllIssues(@Header("Authorization") token_string: String): Call<List<IssueItem>>
    }
}
