package com.example.slowclock.notification

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.reflect.Method

object GuardianNotifier {
    fun sendReminderToUser(context: Context, fcmToken: String, title: String, message: String) {
        val json = JSONObject().apply {
            put("to", fcmToken)
            put("notification", JSONObject().apply {
                put("title", title)
                put("body", message)
            })
        }

        val request = object : JsonObjectRequest(
            Method.POST,
            "https://fcm.googleapis.com/fcm/send",
            json,
            { Log.d("FCM", "Push sent successfully") },
            { error -> Log.e("FCM", "Push failed", error) }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf(
                    "Authorization" to "key=bc941e205006845caa3d8c03dc12609e49610dee\n" +
                            "0e5a22a39796a8f2ee0e1d6d1ae2e7c425662f65", // 🔐 Firebase 프로젝트의 서버 키로 바꾸세요
                    "Content-Type" to "application/json"
                )
            }
        }

        Volley.newRequestQueue(context).add(request)
    }
}
