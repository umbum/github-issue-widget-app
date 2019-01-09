package com.tistory.umbum.github_issue_widget_app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.tistory.umbum.github_issue_widget_app.helper.GithubService
import com.tistory.umbum.github_issue_widget_app.helper.openCustomTab
import com.tistory.umbum.github_issue_widget_app.model.AccessTokenResponse

import java.util.ArrayList

import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val DBG_TAG = "UMBUMDBG"

class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var mAuthTask: UserLoginTask? = null
    val client_id = "69ca379e3b147d012f8b"
    val client_secret = "9e9551cf568fa36f765bda32262469deb328687d"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Set up the login form.
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        val uri = Uri.Builder()
                .scheme("https")
                .appendPath("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", "69ca379e3b147d012f8b")
                .appendQueryParameter("redirect_uri", "github-issue-widget://login")
                .appendQueryParameter("scope", "repo")
                .build()

        // request OAuth "code"
        openCustomTab(this, uri)
//        email_sign_in_button.setOnClickListener { attemptLogin() }
    }

    override fun onResume() {
        // 로그인 완료되면 여기로 들어옴. 근데 다시 CCT가 실행되면서 무한루프에 빠짐.
        super.onResume()
        val intent = getIntent()
        if (intent != null && Intent.ACTION_VIEW.equals(intent.action)) {
            val uri = intent.data
            if (uri != null) {
                val code = uri.getQueryParameter("code")
                Log.d(DBG_TAG, "onResume() ${code}")
            }
        }
        else {
            Log.d(DBG_TAG, "onResume intent is null")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        // github 측에서 github-issue-widget://login?code=...로 code를 보내주는데, 이 때 호출 된다.
        // xml에 singleTask가 지정되어 있어 onCreate가 아니라 onNewIntent가 호출됨.
        super.onNewIntent(intent)
        Log.d(DBG_TAG, "onNewIntent")
        if (intent != null && Intent.ACTION_VIEW.equals(intent.action)) {
            val uri = intent.data
            if (uri != null) {
                val code = uri.getQueryParameter("code")
                Log.d(DBG_TAG, "onNewIntent ${code}")

                val retrofit = Retrofit.Builder()
                        .baseUrl("https://github.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                val ghService = retrofit.create(GithubService::class.java)
                val request = ghService.requestAccessToken(client_id, client_secret, code)
                request.enqueue(object : Callback<AccessTokenResponse> {
                    override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                        Log.d(DBG_TAG, "retrofit onFailure: " + t.toString())
                    }

                    override fun onResponse(call: Call<AccessTokenResponse>, response: Response<AccessTokenResponse>) {
                        Log.d(DBG_TAG, response.body().toString())
                        val access_token = response.body()?.access_token ?: return
                        val sharedPreferences = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("access_token", access_token)
                        editor.apply()
                        // 위젯 update해야겠지?
                    }
                })
            }
        }
        else {
            Log.d(DBG_TAG, "onNewIntent intent is null")
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordStr) || !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            mAuthTask = UserLoginTask(emailStr, passwordStr)
            mAuthTask!!.execute(null as Void?)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 7
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    object ProfileQuery {
        val PROJECTION = arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
        val ADDRESS = 0
        val IS_PRIMARY = 1
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    inner class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            // request login
            Log.d(DBG_TAG, mEmail)
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val ghService = retrofit.create(GithubService::class.java)
            val request = ghService.requestUserRepos("umbum")
            val result = request.execute().body()
            Log.d(DBG_TAG, result.toString())

            return true
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null
            showProgress(false)

            if (success!!) {
                // 요기서 intent로 값을 돌려줘야지. 나를 부른 곳 한테.
                finish()
            } else {
                password.error = getString(R.string.error_incorrect_password)
                password.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }
}
