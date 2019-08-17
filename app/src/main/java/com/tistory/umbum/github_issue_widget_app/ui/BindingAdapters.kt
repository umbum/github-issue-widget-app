package com.tistory.umbum.github_issue_widget_app.ui

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import com.tistory.umbum.github_issue_widget_app.data.model.RepoItem
import com.tistory.umbum.github_issue_widget_app.ui.reposelect.RepoSelectAdapter

/**
 * 호출시점은, RepoSelectActivity의 onCreate의 종료 직후 초기화를 위해 한 번 호출되고
 * retrofit이 끝나고 나서 repo를 받아온 시점에 viewModel의 Observable데이터가 변경되면서 두 번째 호출된다.
 */
@BindingAdapter("repo_items")
fun setRepoItems(view: RecyclerView, items: List<RepoItem>) {
    val adapter = view.adapter as? RepoSelectAdapter ?: return
    adapter.items = items
    adapter.notifyDataSetChanged()
}