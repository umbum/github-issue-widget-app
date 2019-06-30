package com.tistory.umbum.github_issue_widget_app.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.tistory.umbum.github_issue_widget_app.view.ALL_ISSUES_NAME
import com.tistory.umbum.github_issue_widget_app.DBG_TAG
import com.tistory.umbum.github_issue_widget_app.R
import com.tistory.umbum.github_issue_widget_app.api.GithubClient
import com.tistory.umbum.github_issue_widget_app.model.IssueItem
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class IssueListService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return IssueListFactory(this.applicationContext, intent)
    }
}


class IssueListFactory(val context: Context, val intent: Intent): RemoteViewsService.RemoteViewsFactory {
    val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    val sharedPreferences = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
    var allIssueFlag = false
    private var issueItems = emptyList<IssueItem>()

    override fun onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
    }

    override fun getLoadingView(): RemoteViews? {
        // TODO("원래 nulltype 아님. 수정할 것")
        return null
    }

    override fun getItemId(position: Int): Long {
        // 원래는 issueItems[position].id 같은걸 반환해야 하지만, 그냥 position을 id로 쓸거면 아래처럼.
        return position.toLong()
    }

    override fun onDataSetChanged() {
        // 이게 호출이 되면, 자동으로 getViewAt을 호출하게 됨.
        Log.d(DBG_TAG, "onDataSetChanged: start / appWidgetId${appWidgetId}")
        val access_token = sharedPreferences.getString("access_token", null)
        if (access_token == null) {
            Log.d(DBG_TAG, "onDataSetChanged: access_token is null")
            return
        }

        val token_string = "token ${access_token}"
        Log.d(DBG_TAG, "IssueListFactory.onDataSetChanged: ${token_string}")

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val service = retrofit.create(GithubClient.ApiService::class.java)
        val request: Call<List<IssueItem>>

        // sharedPreference에서 선택된 repo이름을 가져와야함. appWidgetId 사용해서.
        val repo_full_name = sharedPreferences.getString("selected_repo_for_id${appWidgetId}", ALL_ISSUES_NAME)
        if (repo_full_name != ALL_ISSUES_NAME) {
            val user_and_repo = repo_full_name.split("/")
            val user = user_and_repo[0]
            val repo = user_and_repo[1]
            Log.d(DBG_TAG, "[request] ${user}/${repo}" )
            request = service.requestIssues(token_string, user, repo)
            allIssueFlag = false
        }
        else {
            request = service.requestAllIssues(token_string)
            allIssueFlag = true
        }

        // enqueue로 안하고 execute로 하는건, 이 함수가 끝나고 나서 getCount가 호출되면서 그 크기만큼 getViewAt이 호출되는데
        // 비동기 함수를 쓰면, 이 함수가 바로 종료되어 버리기 때문에 getCount 값이 이전 크기를 리턴해버린다.
        val issues = request.execute().body()
        if (issues != null) {
            Log.d(DBG_TAG, "[receive] issues count ${issues .size}")
            issueItems = issues
        }
        else {
            Log.d(DBG_TAG, "[receive] issues == null. something wrong.")
        }
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        // position will always range from 0 to getCount() - 1.
        /**
         * You can do heaving lifting in here, synchronously.
         * For example, process an image, fetch something from the network, etc.
         * A loading view will show up in lieu of the actual contents in the interim
         */

        // setViewVisibility로 issue_repository를 안보이게 만들 수 있긴 한데, padding처리를 따로 해줘야해서 지저분하고, 아래처럼 처리하는게 더 좋은 방법이다.
        val view = RemoteViews(context.packageName, if (allIssueFlag) R.layout.issue_item_title_repo else R.layout.issue_item_only_title)
        view.setTextViewText(R.id.issue_title, issueItems[position].title)
        if (allIssueFlag) {
            view.setTextViewText(R.id.issue_repository, issueItems[position].repository.full_name)
        }

        // 위젯 버튼에 웹브라우저로 연결하는 이벤트 달기.
        // 이 것도 Chrome Custom Tab으로 해버리지 뭐. 그래야 앱에 자연스럽게 포함된 기능같이 보이니까.
        // 이렇게 등록하면, Provider의 onUpdate에서 등록한 PendingIntent 대로 Intent가 발생한다.
        val intent = Intent()
        intent.putExtra("url", issueItems[position].html_url)
        view.setOnClickFillInIntent(R.id.issue_item, intent)

        // Return the remote views object.
        return view
    }

    override fun getCount(): Int {
        // 0..getCount()만큼 getViewAt()이 호출된다.
        Log.d(DBG_TAG, "IssueListFactory.getCount: return ${issueItems.size}")
        return issueItems.size
    }

    override fun getViewTypeCount(): Int {
        // issue_item의 layout으로 2종류를 사용하니까, getViewTypeCount는 2를 리턴해야 한다.
        return 2
    }

    override fun onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
    }
}