package com.tistory.umbum.github_issue_widget_app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews

/**
 * IssueWidgetProvider
 */
class IssueWidget : AppWidgetProvider() {

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

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
            /** widget은 런쳐 어플리케이션에 속하는 거라서, RemoteViews를 사용해야 한다.
             타 어플리케이션의 뷰를 컨트롤할 수 없기 때문에, 이 프로세스가 view를 만들어서 표시하는게 아니고,
             뷰를 이렇게 그려 달라는 설계도를 RemoteViews로 반환받아서 여기에 값들을 set하고, 이를
             appWidgetManager를 통해 런쳐 어플리케이션에 전달하면, 런쳐 어플리케이션이 이 뷰를 그리는 방식으로 동작한다.
             */
            val views = RemoteViews(context.packageName, R.layout.issue_widget)

            // 위젯을 update할 필요가 있을 때, issue_list_view내부의 데이터를 만들기 위해서
            // intent를 보내게 되고 이로인해 실행되는 서비스가 IssueWidgetService
            val intent = Intent(context, IssueWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)))
            // update 시 issue_list_view 데이터를 만들기 위해 intent를 보내도록 등록.
            views.setRemoteAdapter(R.id.issue_list_view, intent)

            // issue_item이 하나도 없을 때 출력할 뷰.
            views.setEmptyView(R.id.issue_list_view, R.id.issue_empty_view)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

