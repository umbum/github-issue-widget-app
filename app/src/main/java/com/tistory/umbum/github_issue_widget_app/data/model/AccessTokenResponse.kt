package com.tistory.umbum.github_issue_widget_app.data.model

import com.google.gson.annotations.SerializedName

data class AccessTokenResponse(@SerializedName("access_token") val accessToken: String,
                               @SerializedName("token_type") val tokenType: String,
                               @SerializedName("scope") val scope: String)