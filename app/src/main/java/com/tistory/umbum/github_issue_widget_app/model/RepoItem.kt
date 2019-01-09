package com.tistory.umbum.github_issue_widget_app.model


data class RepoItem(val id: Int,
                    val name: String,
                    val full_name: String,
                    val private: Boolean,
                    val open_issues_count: Int)