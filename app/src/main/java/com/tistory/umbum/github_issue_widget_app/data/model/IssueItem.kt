package com.tistory.umbum.github_issue_widget_app.data.model

import com.google.gson.annotations.SerializedName

data class IssueItem(@SerializedName("id") val id: Int,
                     @SerializedName("title") val title: String,
                     @SerializedName("html_url") val htmlUrl: String,
                     @SerializedName("labels") val labels: List<LabelItem>,
                     @SerializedName("repository") val repository: RepoItem)

data class LabelItem(@SerializedName("name") val name: String,
                     @SerializedName("color") val color: String)

data class RepoItem(@SerializedName("id") val id: Int,
                    @SerializedName("name") val name: String,
                    @SerializedName("full_name") val fullName: String,
                    @SerializedName("private") val private: Boolean,
                    @SerializedName("open_issues_count") val openIssuesCount: Int)