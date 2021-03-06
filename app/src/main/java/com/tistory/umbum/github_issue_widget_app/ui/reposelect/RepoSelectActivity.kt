package com.tistory.umbum.github_issue_widget_app.ui.reposelect

import android.appwidget.AppWidgetManager
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.tistory.umbum.github_issue_widget_app.R
import com.tistory.umbum.github_issue_widget_app.databinding.ActivityRepoSelectBinding

class RepoSelectActivity : AppCompatActivity() {
    val TAG = this::class.java.simpleName

    private val viewModel: RepoSelectViewModel by lazy {
        ViewModelProviders
                .of(this, RepoSelectViewModelFactory(this.application))
                .get(RepoSelectViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_select)

        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.d(TAG, "RepoSelectActivity: appWidgetId is INVALID")
            return
        }

        val binding: ActivityRepoSelectBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_select)
        binding.vm = viewModel
        binding.repoListView.layoutManager = LinearLayoutManager(this)
        binding.repoListView.itemAnimator = DefaultItemAnimator()
        binding.repoListView.adapter = RepoSelectAdapter(appWidgetId)
    }

}

