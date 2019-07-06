package com.tistory.umbum.github_issue_widget_app.viewmodel

import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.view.View
import com.tistory.umbum.github_issue_widget_app.view.OAuthLoginActivity

/**
 * ViewModel에 View가 섞여버리는건 좋지 않다고 하는데
 * Activity에서 다른 Activity를 시작해버리면 유닛테스트가 어렵다는 단점이 있다.
 * 다른 Activity를 시작하는건 크게 3가지로 나눌 수 있을 것 같은데
 * 1. View를 전달받은 ViewModel이 처리하기
 * 2. Activity에서 처리하기
 * 3. Navigator 패턴 사용하기.
 */
class ConfigViewModel : ViewModel() {
    fun onLogin(v: View) {

        val intent = Intent(v.context, OAuthLoginActivity::class.java)
        v.context.startActivity(intent)
    }
}