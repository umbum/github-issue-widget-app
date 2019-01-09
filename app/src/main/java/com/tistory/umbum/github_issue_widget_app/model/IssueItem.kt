package com.tistory.umbum.github_issue_widget_app.model

data class IssueItem(val id: Int,
                     val title: String,
                     val html_url: String,
                     val labels: List<LabelItem>
                     )

data class LabelItem(val name: String,
                     val color: String)