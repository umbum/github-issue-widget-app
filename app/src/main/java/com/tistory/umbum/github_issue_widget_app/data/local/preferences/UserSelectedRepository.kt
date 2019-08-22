package com.tistory.umbum.github_issue_widget_app.data.local.preferences

import android.content.Context
import android.util.Log
import com.tistory.umbum.github_issue_widget_app.ALL_ISSUES_TEXT

class UserSelectedRepository(private val context: Context) {
    private val TAG = this::class.java.simpleName
    private val keyPrefix = "selected_repo_for_id"
    private val sharedPref = context.applicationContext
            .getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)

    fun getSelectedRepoPath(id: Int) : String = sharedPref
                .getString("${keyPrefix}${id}", ALL_ISSUES_TEXT)!!

    fun setSelectedRepoPath(id: Int, repoPath: String) {
        sharedPref.edit()
                .putString("${keyPrefix}${id}", repoPath)
                .apply()
        Log.d(TAG, "[save] ${keyPrefix}${id} : ${repoPath}")
    }

    fun removeSelectedRepoPath(id: Int) {
        sharedPref.edit()
                .remove("${keyPrefix}${id}")
                .apply()
    }
}
