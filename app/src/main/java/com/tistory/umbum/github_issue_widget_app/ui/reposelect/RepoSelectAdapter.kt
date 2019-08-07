package com.tistory.umbum.github_issue_widget_app.ui.reposelect

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tistory.umbum.github_issue_widget_app.DBG_TAG
import com.tistory.umbum.github_issue_widget_app.R
import com.tistory.umbum.github_issue_widget_app.databinding.RepoItemBinding
import com.tistory.umbum.github_issue_widget_app.data.model.RepoItem
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
        repoViewHolder.onBind(position)
    }

    inner class RepoViewHolder(view: View): RecyclerView.ViewHolder(view), RepoItemViewModel.RepoItemViewModelListener {
        val binding: RepoItemBinding = DataBindingUtil.bind(view)!!

        fun onBind(position: Int) {
            binding.vm = RepoItemViewModel(items.get(position), this)
        }

        override fun onItemClick(fullName: String) {
            val context = itemView.context
            val activity = context as Activity
            val sharedPreferences = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)

            val editor = sharedPreferences.edit()
            editor.putString("selected_repo_for_id${appWidgetId}", fullName)
            editor.apply()
            Log.d(DBG_TAG, "[save] selected_repo_for_id${appWidgetId} : ${fullName}")

            val updateIntent = Intent(context, IssueWidget::class.java)
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            // widget의 textView에 설정할 값은 extra로 전달해도 되긴 되는데, 그냥 sharedPreference에서 읽어오면 된다.
            updateIntent.putExtra("repo_select_btn_text", fullName)
            activity.sendBroadcast(updateIntent)
            activity.finish()
        }
    }
}
