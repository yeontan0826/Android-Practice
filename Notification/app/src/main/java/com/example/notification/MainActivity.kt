package com.example.notification

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val resultTextview: TextView by lazy {
        findViewById(R.id.resultTextView)
    }

    private val firebaseToken: TextView by lazy {
        findViewById(R.id.firebaseTokenTextView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFirebase()
        updateResult()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        updateResult(true)
    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseToken.text = task.result
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateResult(isNewIntent: Boolean = false) {
        resultTextview.text = (intent.getStringExtra("notification type") ?: "앱 런처") +
                if (isNewIntent) {
                    "(으)로 갱신했습니다"
                } else {
                    "(으)로 실행했습니다"
                }
    }
}