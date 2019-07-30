package com.tistory.umbum.github_issue_widget_app.data.remote.api

import com.tistory.umbum.github_issue_widget_app.data.model.AccessTokenResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST


const val GITHUB_URL = "https://github.com/"

object GithubClient {
    val client = Retrofit.Builder()
            .baseUrl(GITHUB_URL)
//            .client(OkHttpClient())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Service::class.java)

    interface Service {
        @Headers("Accept: application/json")
        @FormUrlEncoded
        @POST("login/oauth/access_token")
        fun requestAccessToken(@Field("client_id") client_id: String,
                               @Field("client_secret") client_secret: String,
//                           @Field("state") state: String,
//                           @Field("redirect_uri") redirect_uri: String,
                               @Field("code") code: String): Call<AccessTokenResponse>
    }


}


