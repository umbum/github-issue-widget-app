package com.tistory.umbum.github_issue_widget_app.view

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tistory.umbum.github_issue_widget_app.R
import com.tistory.umbum.github_issue_widget_app.databinding.ActivityConfigBinding
import com.tistory.umbum.github_issue_widget_app.viewmodel.ConfigViewModel

class ConfigActivity : AppCompatActivity() {
    private val viewModel: ConfigViewModel by lazy {
        ViewModelProviders.of(this).get(ConfigViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityConfigBinding = DataBindingUtil.setContentView(this, R.layout.activity_config);
        binding.viewmodel = viewModel
    }
}
