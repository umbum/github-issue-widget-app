package com.tistory.umbum.github_issue_widget_app.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.tistory.umbum.github_issue_widget_app.ALL_ISSUES_TEXT
import com.tistory.umbum.github_issue_widget_app.R
import com.tistory.umbum.github_issue_widget_app.data.local.preferences.AccessTokenRepository
import com.tistory.umbum.github_issue_widget_app.data.local.preferences.UserSelectedRepository
import com.tistory.umbum.github_issue_widget_app.data.remote.api.GithubApiClient
import com.tistory.umbum.github_issue_widget_app.data.model.IssueItem
import retrofit2.Call


class IssueListService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return IssueListFactory(this.applicationContext, intent)
    }
}


class IssueListFactory(val context: Context, val intent: Intent): RemoteViewsService.RemoteViewsFactory {
    val TAG = this::class.java.simpleName

    private val userSelectedRepository = UserSelectedRepository(context)
    private val accessTokenRepository = AccessTokenRepository(context)
    private val githubApiClient = GithubApiClient.client

    private var issueItems = emptyList<IssueItem>()

    val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    var allIssueFlag = false

    override fun onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        // 원래는 issueItems[position].id 같은걸 반환해야 하지만, 그냥 position을 id로 쓸거면 아래처럼.
        return position.toLong()
    }

    override fun onDataSetChanged() {
        Log.d(TAG, "onDataSetChanged: start / appWidgetId${appWidgetId}")
        val accessToken = accessTokenRepository.accessToken
        if (accessToken == null) {
            Log.d(TAG, "onDataSetChanged: accessToken is null")
            return
        }

        val tokenString = "token ${accessToken}"
        Log.d(TAG, "IssueListFactory.onDataSetChanged: ${tokenString}")

        val repoPath = userSelectedRepository.getSelectedRepoPath(appWidgetId)
        val request: Call<List<IssueItem>>
        if (repoPath != ALL_ISSUES_TEXT) {
            val (user, repo) = repoPath.split("/")
            request = githubApiClient.getUserIssues(tokenString, user, repo)
            allIssueFlag = false
        }
        else {
            request = githubApiClient.getAllMyIssues(tokenString)
            allIssueFlag = true
        }

        // enqueue로 안하고 execute로 하는건, 이 함수가 끝나고 나서 getCount가 호출되면서 그 크기만큼 getViewAt이 호출되는데
        // 비동기 함수를 쓰면 이 함수가 바로 종료되어 버리기 때문에 getCount 값이 이전 크기를 리턴해버린다.
        val issues = request.execute().body()
        if (issues != null) {
            Log.d(TAG, "[receive] the number of issues = ${issues.size}")
            issueItems = issues
        }
        else {
            Log.d(TAG, "[receive] issues == null. something wrong.")
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

        val view = RemoteViews(context.packageName, if (allIssueFlag) R.layout.issue_item_title_repo else R.layout.issue_item_only_title)
        view.setTextViewText(R.id.issue_title, issueItems[position].title)
        if (allIssueFlag) {
            view.setTextViewText(R.id.issue_repository, issueItems[position].repository.fullName)
        }

        // 위젯 버튼에 웹브라우저로 연결하는 이벤트 달기.
        // 이렇게 등록하면, Provider의 onUpdate에서 등록한 PendingIntent 대로 Intent가 발생한다.
        val intent = Intent()
        intent.putExtra("url", issueItems[position].htmlUrl)
        view.setOnClickFillInIntent(R.id.issue_item, intent)

        // Return the remote views object.
        return view
    }

    override fun getCount(): Int {
        // 0..getCount()만큼 getViewAt()이 호출된다.
        return issueItems.size
    }

    override fun getViewTypeCount(): Int {
        return 2 // issue_item의 layout으로 2종류를 사용하고 있다.
    }

    override fun onDestroy() {
        userSelectedRepository.removeSelectedRepoPath(appWidgetId);
    }
}