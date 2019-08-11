 package com.tistory.umbum.github_issue_widget_app.data.local.preferences

 import android.content.Context

 /**
 *       Repository           < 어디서 데이터를 가져올 지를 추상화하는 역할. DB에서 가져올지, SharedPref에서 가져올지 >
 * -----------------------
 * SharedPreferencesHelper    < SharedPreferences 관련 코드를 숨기는 역할. DI하면 context를 여기서만 받아도 된다. >
 * -----------------------
 *    SharedPreferences
 */
class SharedPreferencesHelper(private val context: Context) {

}