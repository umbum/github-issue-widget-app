package com.tistory.umbum.github_issue_widget_app

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tistory.umbum.github_issue_widget_app.model.RepoItem
import com.tistory.umbum.github_issue_widget_app.widget.IssueWidget
import kotlinx.android.synthetic.main.repo_item.view.*

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val name = view.findViewById<TextView>(R.id.repo_full_name)
}

class RepoSelectAdapter(private val activity: Activity, private val items: List<RepoItem>, private val appWidgetId: Int)
    : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.repo_item, parent, false)
        val sharedPreferences = activity.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)

        itemView.setOnClickListener {
            // 클릭이 되었으면,
            // 선택된 레포를 저장하고 (name을 저장해야... 위젯의 text view에 띄워줄거니까.)
            // update하라고 알려주기.
            val editor = sharedPreferences.edit()
            editor.putString("selected_repo_for_id${appWidgetId}", it.repo_full_name.text.toString())
            editor.apply()
            Log.d(DBG_TAG, "[save] selected_repo_for_id${appWidgetId} : ${it.repo_full_name.text}")

            // 선택된 repo가 변경되었으니 widget의 textView 변경하고, issue_list 업데이트하라고 알려주기.
            val updateIntent = Intent(activity, IssueWidget::class.java)
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            // widget의 textView에 설정할 값은 extra로 전달해도 되긴 되는데, 그냥 sharedPreference에서 읽어오면 된다.
//            updateIntent.putExtra("repo_select_btn_text", it.repo_full_name.text)
            activity.sendBroadcast(updateIntent)
            activity.finish()
        }

        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, idx: Int) {
        viewHolder.name.text = items[idx].full_name
    }

}
