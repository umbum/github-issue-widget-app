package com.tistory.umbum.github_issue_widget_app.view

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tistory.umbum.github_issue_widget_app.R
import com.tistory.umbum.github_issue_widget_app.databinding.ActivityConfigBinding
import com.tistory.umbum.github_issue_widget_app.viewmodel.ConfigViewModel

class ConfigActivity : AppCompatActivity() {
    val configViewModel = ConfigViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding: ActivityConfigBinding = DataBindingUtil.setContentView(this, R.layout.activity_config);
        dataBinding.viewmodel = configViewModel;
    }
}
