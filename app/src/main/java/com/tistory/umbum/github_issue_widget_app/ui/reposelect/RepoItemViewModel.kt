package com.tistory.umbum.github_issue_widget_app.ui.reposelect

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import com.tistory.umbum.github_issue_widget_app.data.model.RepoItem


class RepoItemViewModel(private val repoItem: RepoItem, private val listener: RepoItemViewModelListener): ViewModel() {
    val id = ObservableInt(repoItem.id)
    val name = ObservableField<String>(repoItem.name)
    val fullName = ObservableField<String>(repoItem.fullName)
    val private = ObservableBoolean(repoItem.private)
    val openIssuesCount = ObservableInt(repoItem.openIssuesCount)

    fun onItemClick() {
        listener.onItemClick(repoItem.fullName)
    }

    interface RepoItemViewModelListener {
        fun onItemClick(fullName: String)
    }
}