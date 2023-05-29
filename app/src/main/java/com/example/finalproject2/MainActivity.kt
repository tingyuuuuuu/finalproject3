package com.example.finalproject2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap
import android.view.View
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fab: View = findViewById(R.id.floatingActionButton)


        //OkHttp調用
        //Read api
        val client = OkHttpClient()
        val urlBuilder = "http://10.0.2.2/app/read.php".toHttpUrlOrNull()
            ?.newBuilder()
            ?.addQueryParameter("title", "st")
            ?.addQueryParameter("limit", "50")
        val url = urlBuilder?.build().toString()
        val request = Request.Builder()
            .url(url)
            .build()


        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful){
                    val responseBody = response.body?.string()
                    //println("-------${responseBody}")

                    val jsonObject = JSONObject(responseBody)
                    val dataArray = jsonObject.getJSONArray("data")


                    // 將以 Read api 讀取到的資料用到 gridView 的方式排列，使其更像備忘錄
                    // 並設定當 gridView 按下時，可跳轉畫面至 MainActivity (代表編輯頁面)
                    // 找到欄位序列，將資料以 Hashmap 的形式傳送，再到 MainActivity 做處理資料
                    runOnUiThread {
                        val gridView = findViewById<GridView>(R.id.gridView)
                        val grid = ArrayList<HashMap<String, Any>>()
                        val list: MutableList<String> = ArrayList()
                        for (i in 0 until dataArray.length()){
                            val chatObject = dataArray.getJSONObject(i)
                            //val no = chatObject.getString("no")
                            val timestamp = chatObject.getString("timestamp")

                            val title = chatObject.getString("title")
                            val content = chatObject.getString("content")
                            list.add("${title} ${content}\n")
                        }

                        for (i in list.indices){
                            val map = HashMap<String, Any>()
                            map["title"] = list[i]
                            grid.add(map)
                        }
                        val fromData = arrayOf("title")
                        val gridlength = grid.size
                        //println("gridlength = " + gridlength)
                        val toData = intArrayOf(R.id.textView6)
                        val simpleAdapter = SimpleAdapter(this@MainActivity, grid, R.layout.grid_item, fromData, toData)
                        gridView.adapter = simpleAdapter
                        gridView.setOnItemClickListener { adapterView, view, i, l ->
                            val intent = Intent()
                            intent.setClass(this@MainActivity, MainActivity2::class.java)
                            intent.putExtra("no", gridlength-i)
                            intent.putExtra("title",grid[i])
                            startActivity(intent)
                        }
                        fab.setOnClickListener {
                            val intent = Intent()
                            intent.setClass(this@MainActivity, MainActivity2::class.java)
                            startActivity(intent)
                        }
                    }
                }
                else{
                    println("Request failed")
                    runOnUiThread {
                        findViewById<TextView>(R.id.textView6).text = "資料錯誤"
                    }
                }
            }
        })


        val timer = Timer()
        val timerTask = object : TimerTask(){
            override fun run(){
                val client = OkHttpClient()
                val urlBuilder = "http://10.0.2.2/app/read.php".toHttpUrlOrNull()
                    ?.newBuilder()
                    ?.addQueryParameter("title", "st")
                    ?.addQueryParameter("limit", "50")
                val request = Request.Builder()
                    .url(url)
                    .build()
                client.newCall(request).enqueue(object : Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }
                    override fun onResponse(call: Call, response: Response) {
                        if(response.isSuccessful){
                            val responseBody = response.body?.string()
                            //println("-------${responseBody}")

                            val jsonObject = JSONObject(responseBody)
                            val dataArray = jsonObject.getJSONArray("data")


                            runOnUiThread {
                                val gridView = findViewById<GridView>(R.id.gridView)
                                val grid = ArrayList<HashMap<String, Any>>()
                                val list: MutableList<String> = ArrayList()
                                for (i in 0 until dataArray.length()){
                                    val chatObject = dataArray.getJSONObject(i)
                                    //val no = chatObject.getString("no")
                                    val timestamp = chatObject.getString("timestamp")

                                    val title = chatObject.getString("title")
                                    val content = chatObject.getString("content")
                                    list.add(" ${title} ${content}\n")
                                }

                                for (i in list.indices){
                                    val map = HashMap<String, Any>()
                                    map["title"] = list[i]
                                    grid.add(map)
                                }
                                val fromData = arrayOf("title")
                                val gridlength = grid.size
                                //println("gridlength = " + gridlength)
                                val toData = intArrayOf(R.id.textView6)
                                val simpleAdapter = SimpleAdapter(this@MainActivity, grid, R.layout.grid_item, fromData, toData)
                                gridView.adapter = simpleAdapter
                                gridView.setOnItemClickListener { adapterView, view, i, l ->
                                    val intent = Intent()
                                    intent.setClass(this@MainActivity, MainActivity2::class.java)
                                    intent.putExtra("no", gridlength-i)
                                    intent.putExtra("title",grid[i])
                                    startActivity(intent)
                                }
                                fab.setOnClickListener {
                                    val intent = Intent()
                                    intent.setClass(this@MainActivity, MainActivity2::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                        else{
                            println("Request failed")
                            runOnUiThread {
                                findViewById<TextView>(R.id.textView6).text = "資料錯誤"
                            }
                        }
                    }
                })
            }
        }
        timer.schedule(timerTask, 0, 5000)

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item.getItemId()
        if (id == R.id.delete) {
            //刪除指令放放這裡;
            return true;
        } else if (id == R.id.about) {
            Toast.makeText(this, "關於", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item)
    }
}