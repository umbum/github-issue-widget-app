package com.tistory.umbum.github_issue_widget_app.model

data class AccessTokenResponse(val access_token: String,
                               val token_type: String,
                               val scope: String)