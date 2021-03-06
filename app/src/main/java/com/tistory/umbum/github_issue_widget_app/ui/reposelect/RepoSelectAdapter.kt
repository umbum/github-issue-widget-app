package com.tistory.umbum.github_issue_widget_app.ui.reposelect

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tistory.umbum.github_issue_widget_app.R
import com.tistory.umbum.github_issue_widget_app.data.local.preferences.UserSelectedRepository
import com.tistory.umbum.github_issue_widget_app.data.model.RepoItem
import com.tistory.umbum.github_issue_widget_app.databinding.RepoItemBinding
import com.tistory.umbum.github_issue_widget_app.ui.widget.IssueWidget


class RepoSelectAdapter(private val appWidgetId: Int)
    : RecyclerView.Adapter<RepoSelectAdapter.RepoViewHolder>() {

    var items = emptyList<RepoItem>()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.repo_item, parent, false)
        return RepoViewHolder(itemView)
    }


    override fun onBindViewHolder(repoViewHolder: RepoViewHolder, position: Int) {
        repoViewHolder.onBind(items.get(position))
    }

    inner class RepoViewHolder(view: View): RecyclerView.ViewHolder(view), RepoItemViewModel.RepoItemViewModelListener {
        val binding: RepoItemBinding = DataBindingUtil.bind(view)!!
        private val userSelectedRepository = UserSelectedRepository(itemView.context)

        fun onBind(item: RepoItem) {
            binding.vm = RepoItemViewModel(item, this)
        }

        override fun onItemClick(fullName: String) {
            val context = itemView.context
            val activity = context as Activity

            userSelectedRepository.setSelectedRepoPath(appWidgetId, fullName)

            val updateIntent = Intent(context, IssueWidget::class.java)
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            // widget의 textView에 설정할 값은 extra로 전달해도 되지만, sharedPreference에서 읽어온다.
            // prefs에 저장된 값이 있는 상태에서 로그아웃만 했다면, 로그인 했을 때 레포 설정해둔게 유지되면 좋으니까.
            activity.sendBroadcast(updateIntent)
            activity.finish()
        }
    }
}
