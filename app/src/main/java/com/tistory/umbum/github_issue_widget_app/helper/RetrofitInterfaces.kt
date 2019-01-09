package com.tistory.umbum.github_issue_widget_app.helper

import com.google.gson.JsonArray
import com.tistory.umbum.github_issue_widget_app.model.AccessTokenResponse
import com.tistory.umbum.github_issue_widget_app.model.IssueItem
import com.tistory.umbum.github_issue_widget_app.model.RepoItem
import retrofit2.Call
import retrofit2.http.*


interface GithubService {
    @GET("users/{user}/repos")
    fun requestUserRepos(@Path("user") user: String): Call<JsonArray>

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("login/oauth/access_token")
    fun requestAccessToken(@Field("client_id") client_id: String,
                           @Field("client_secret") client_secret: String,
//                           @Field("state") state: String,
//                           @Field("redirect_uri") redirect_uri: String,
                           @Field("code") code: String): Call<AccessTokenResponse>

    @GET("/user/repos")
    fun requestAccountRepos(@Header("Authorization") token_string: String): Call<List<RepoItem>>
//    TODO : Header를 OkHttp interceptor를 이용해 처리하기.

    @GET("/repos/{user}/{repo}/issues")
    fun requestIssues(@Header("Authorization") token_string: String,
                      @Path("user") user: String,
                      @Path("repo") repo: String): Call<List<IssueItem>>

    @GET("/issues")
    fun requestAllIssues(@Header("Authorization") token_string: String): Call<List<IssueItem>>
}



