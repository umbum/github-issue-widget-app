package com.tistory.umbum.github_issue_widget_app.data.model

data class IssueItem(val id: Int,
                     val title: String,
                     val html_url: String,
                     val labels: List<LabelItem>,
                     val repository: RepoItem
                     )

data class LabelItem(val name: String,
                     val color: String)

data class RepoItem(val id: Int,
                    val name: String,
                    val full_name: String,
                    val private: Boolean,
                    val open_issues_count: Int)