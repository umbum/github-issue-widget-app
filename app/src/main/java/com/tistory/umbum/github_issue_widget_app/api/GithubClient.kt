package com.tistory.umbum.github_issue_widget_app.api

import com.google.gson.JsonArray
import com.tistory.umbum.github_issue_widget_app.model.AccessTokenResponse
import com.tistory.umbum.github_issue_widget_app.model.IssueItem
import com.tistory.umbum.github_issue_widget_app.model.RepoItem
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL = "https://github.com/"

object GithubClient {
    val githubApi = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    interface ApiService {
        @GET("users/{user}/repos")
        fun requestUserRepos(@Path("user") user: String): Call<JsonArray>

        @Headers("Accept: application/json")
        @FormUrlEncoded
        @POST("login/oauth/access_token")
        fun requestAccessToken(@Field("client_id") client_id: String,
                               @Field("client_secret") client_secret: String,
//                           @Field("state") state: String,
//                           @Field("redirect_uri") redirect_uri: String,
                               @Field("code") code: String): Single<AccessTokenResponse>

        //    TODO : Header를 OkHttp interceptor를 이용해 처리하기.
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


