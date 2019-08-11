package com.tistory.umbum.github_issue_widget_app.data.local.preferences

import android.content.Context
import android.util.Log
import com.tistory.umbum.github_issue_widget_app.ALL_ISSUES_TEXT

class UserSelectedRepository(private val context: Context) {
    val TAG = this::class.java.simpleName

    fun getSelectedRepoPath(id: Int) : String = context.applicationContext
                .getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
                .getString("selected_repo_for_id${id}", ALL_ISSUES_TEXT)!!

    fun setSelectedRepoPath(id: Int, repoPath: String) {
        context.applicationContext
                .getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
                .edit()
                .putString("selected_repo_for_id${id}", repoPath)
                .apply()
        Log.d(TAG, "[save] selected_repo_for_id${id} : ${repoPath}")
    }

}
