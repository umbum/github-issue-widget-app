package com.tistory.umbum.github_issue_widget_app

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class IssueWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return IssueWidgetFactory(this.applicationContext, intent)
    }
}

data class IssueItem(val name:String)

class IssueWidgetFactory(val context: Context, val intent: Intent): RemoteViewsService.RemoteViewsFactory {
    val _count = 3
    val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    val issueItems = ArrayList<IssueItem>()

    override fun onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        for (i in 0 until count) {
            issueItems.add(IssueItem(i.toString() + "!"))
        }
        // We sleep for 3 seconds here to show how the empty view appears in the interim.
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
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
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        Log.d(this::class.java.name, "[*] getViewAt!!!~")
        // position will always range from 0 to getCount() - 1.

        // We construct a remote views item based on our widget item xml file,
        // and set the text based on the position.
        val view = RemoteViews(context.packageName, R.layout.issue_item)
        view.setTextViewText(R.id.issue_item, issueItems[position].name)

        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
//        Bundle extras = new Bundle();
//        extras.putInt(StackWidgetProvider.EXTRA_ITEM, position);
//        Intent fillInIntent = new Intent();
//        fillInIntent.putExtras(extras);
//        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

        // You can do heaving lifting in here, synchronously. For example, if you need to
        // process an image, fetch something from the network, etc., it is ok to do it here,
        // synchronously. A loading view will show up in lieu of the actual contents in the
        // interim.
        try {
            System.out.println("Loading view " + position)
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // Return the remote views object.
        return view
    }

    override fun getCount(): Int {
        return _count
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        Log.d(this::class.java.name, "onDestroy~!")
        issueItems.clear()
    }
}