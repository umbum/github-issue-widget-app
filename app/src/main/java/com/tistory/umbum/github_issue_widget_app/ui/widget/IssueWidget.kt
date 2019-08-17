package com.tistory.umbum.github_issue_widget_app.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.tistory.umbum.github_issue_widget_app.R
import com.tistory.umbum.github_issue_widget_app.data.local.preferences.UserSelectedRepository
import com.tistory.umbum.github_issue_widget_app.ui.config.ConfigActivity
import com.tistory.umbum.github_issue_widget_app.ui.reposelect.RepoSelectActivity
import com.tistory.umbum.github_issue_widget_app.util.openCustomTab


const val ACTION_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"
const val ACTION_CLICK = "android.appwidget.action.ISSUE_CLICK"
/**
 * IssueWidgetProvider
 */
class IssueWidget : AppWidgetProvider() {
    val TAG = this::class.java.simpleName

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_UPDATE -> {
                val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
                else {
                    Log.d(TAG, "onReceive(broadcast receive) : INVALID_APPWIDGET_ID")
                }
            }
            ACTION_CLICK -> {
                val url = intent.extras?.getString("url")
                url?.let { openCustomTab(context, url, FLAG_ACTIVITY_NEW_TASK) } ?: Log.d(TAG, "[onReceive] url is null")
            }
            else -> {
                Log.d(TAG, "[onReceive] action = ${intent.action}")
            }
        }
    }

    companion object {

        val TAG = this::class.java.simpleName

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
            /** widget은 런쳐 어플리케이션에 속하는 거라서, RemoteViews를 사용해야 한다.
             타 어플리케이션의 뷰를 컨트롤할 수 없기 때문에, 이 프로세스가 view를 만들어서 표시하는게 아니고,
             뷰를 이렇게 그려 달라는 설계도(RemoteViews)에 값들을 set하고, 이를
             appWidgetManager를 통해 런쳐 어플리케이션에 전달하면, 런쳐 어플리케이션이 이 뷰를 그리는 방식으로 동작한다.
             */
            val views = RemoteViews(context.packageName, R.layout.issue_widget)
            Log.d(TAG, "IssueWidget.onUpdate: id ${appWidgetId}")

            val userSelectedRepository = UserSelectedRepository(context)
            val repoPath = userSelectedRepository.getSelectedRepoPath(appWidgetId)
            views.setTextViewText(R.id.repo_select_btn, repoPath)
            Log.d(TAG, "[set repo btn text] ${repoPath}")

            // repo select button
            val repoSelectIntent = Intent(context, RepoSelectActivity::class.java)
            repoSelectIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId) // 위젯 마다 다른 repo를 선택할 수 있어야 하므로. 어떤 위젯에서 레포 선택을 호출했는지 정보 필요.
            val repoSelectPendingIntent = PendingIntent.getActivity(context, appWidgetId, repoSelectIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.repo_select_btn, repoSelectPendingIntent)

            // update button
            val updateIntent = Intent(context, IssueWidget::class.java)
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val updatePendingIntent = PendingIntent.getBroadcast(context, appWidgetId, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.update_btn, updatePendingIntent)

            // setting button
            val settingIntent = Intent(context, ConfigActivity::class.java)
            val settingPendingIntent = PendingIntent.getActivity(context, 0, settingIntent, 0)
            views.setOnClickPendingIntent(R.id.setting_btn, settingPendingIntent)

            // issue list view의 데이터를 만들기 위해서 IssueListService에 intent를 보낸다.
            val intent = Intent(context, IssueListService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)))
            // update 시 issue_list_view 데이터를 만들기 위해 intent를 보내도록 등록.
            views.setRemoteAdapter(R.id.issue_list_view, intent)

            /*
            ListView나 StackView의 Item이 클릭되었을 때 어떤 동작을 지정하고 싶다면 3단계를 거쳐야 한다.
            1. Provider의 onUpdate에서 ListView가 클릭되었을 때 어디로 Intent를 보낼 것인지.(R.id.issue_list_view에 대해 PendingIntent 등록.)
            2. Service의 getViewAt에서 FillInIntent를 등록해 아이템 각각에 대한 Intent를 다시 설정.
            3. Intent를 수신하는 쪽에서 적당히 처리.
            이 때 주의할 점. Item이 Button이면 안된다. TextView여야만 하는지는 모르겠는데 TextView로 하면 잘 됨.
             */
            // 리스트뷰 아이템이 클릭되었을 때 어디로 Intent를 보낼지를 지정해야 한다.
            // IssueWidget으로 Intent를 보내게 되고 ACTION_CLICK을 지정했으니, onReceive에서 action으로 구할 수 있다.
            val clickIntent = Intent(context, IssueWidget::class.java)
            clickIntent.setAction(ACTION_CLICK)
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            clickIntent.setData(Uri.parse(clickIntent.toUri(Intent.URI_INTENT_SCHEME)))
            val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.issue_list_view, clickPendingIntent)

            // issue_item이 하나도 없을 때 출력할 뷰.
            views.setEmptyView(R.id.issue_list_view, R.id.issue_empty_view)

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.issue_list_view)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

