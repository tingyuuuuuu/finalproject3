package com.example.finalproject2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import okhttp3.*
import java.io.IOException

class MainActivity2 : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        toolbar = findViewById(R.id.include2)

        setSupportActionBar(toolbar)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
        }

        //設定變數
        val send = findViewById<Button>(R.id.button)
        val title = findViewById<TextView>(R.id.editTextTextPersonName2)
        val content = findViewById<TextView>(R.id.editTextTextPersonName3)
        var no:Int = 0

        intent?.extras?.let {
            val value = it.get("title").toString()
            //println(value)
            title.setText(value.substring(7).split(" ")[0])
            content.setText(value.substring(7).split(" ")[1].split("}")[0])
            no = it.getInt("no")
            //println(no)
        }

        if(title.length() == 0 && content.length() == 0){
            // 當 button 按下時，將資料透過 Post api，建立到資料庫，
            // 並跳到 MainActivity2 (代表備忘錄主頁)
            send.setOnClickListener {
                val postTitle = title.text.toString()
                println("postTitle-------${postTitle}")
                val postContent = content.text.toString()
                println("postContent-----${postContent}")

                val clientPost = OkHttpClient()
                val responseBody = FormBody.Builder()
                    .add("title", "${postTitle}")
                    .add("content", "${postContent}")
                    .build()
                val requestPost = Request.Builder()
                    .url("http://10.0.2.2/app/insert.php")
                    .post(responseBody)
                    .build()
                clientPost.newCall(requestPost).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()
                        println("POST--------${responseBody}")
                    }
                })


                val intent = Intent()
                intent.setClass(this, MainActivity::class.java)
                startActivity(intent)

            }
        }
        else{
            // 當 button 按下時，將資料透過 Post api，建立到資料庫，
            // 並跳到 MainActivity2 (代表備忘錄主頁)
            send.setOnClickListener {

                val postTitle = title.text.toString()
                println("updateTitle-------${postTitle}")
                val postContent = content.text.toString()
                println("updateContent-----${postContent}")

                val postno = no

                val clientPost = OkHttpClient()
                val responseBody = FormBody.Builder()
                    .add("title", "${postTitle}")
                    .add("content", "${postContent}")
                    .add("no", "${postno}")
                    .build()
                val requestPost = Request.Builder()
                    .url("http://10.0.2.2/app/put.php")
                    .post(responseBody)
                    .build()
                clientPost.newCall(requestPost).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()
                        println("Update--------${responseBody}")
                    }
                })
                val intent = Intent()
                intent.setClass(this, MainActivity::class.java)
                startActivity(intent)

            }
        }




    }
}