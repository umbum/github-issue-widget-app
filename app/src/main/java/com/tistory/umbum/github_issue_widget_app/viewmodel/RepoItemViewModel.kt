package com.tistory.umbum.github_issue_widget_app.viewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import com.tistory.umbum.github_issue_widget_app.model.RepoItem


class RepoItemViewModel(private val repoItem: RepoItem, private val listener: RepoItemViewModelListener): ViewModel() {
    val id = ObservableInt(repoItem.id)
    val name = ObservableField<String>(repoItem.name)
    val fullName = ObservableField<String>(repoItem.full_name)
    val private = ObservableBoolean(repoItem.private)
    val openIssuesCount = ObservableInt(repoItem.open_issues_count)

    fun onItemClick() {
        listener.onItemClick(repoItem.full_name)
    }

    interface RepoItemViewModelListener {
        fun onItemClick(fullName: String)
    }
}