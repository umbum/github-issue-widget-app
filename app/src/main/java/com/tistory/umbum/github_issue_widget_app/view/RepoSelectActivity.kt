package com.tistory.umbum.github_issue_widget_app.view

import android.appwidget.AppWidgetManager
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.tistory.umbum.github_issue_widget_app.DBG_TAG
import com.tistory.umbum.github_issue_widget_app.R
import com.tistory.umbum.github_issue_widget_app.databinding.ActivityRepoSelectBinding
import com.tistory.umbum.github_issue_widget_app.viewmodel.RepoSelectViewModel
import com.tistory.umbum.github_issue_widget_app.viewmodel.RepoSelectViewModelFactory

class RepoSelectActivity : AppCompatActivity() {

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
            Log.d(DBG_TAG, "RepoSelectActivity: appWidgetId is INVALID")
            return
        }

        val binding: ActivityRepoSelectBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_select)
        binding.vm = viewModel
        binding.repoListView.layoutManager = LinearLayoutManager(this)
        binding.repoListView.itemAnimator = DefaultItemAnimator()
        binding.repoListView.adapter = RepoSelectAdapter(appWidgetId)
        viewModel.requestRepos()
    }

    /**
     * 뒤로가기 키 누르면 호출됨.
     * RepoSelectActivity가 삭제되면서 RepoSelectViewModel도 삭제된다.
     */
    override fun onDestroy() {
        super.onDestroy()
    }
}

