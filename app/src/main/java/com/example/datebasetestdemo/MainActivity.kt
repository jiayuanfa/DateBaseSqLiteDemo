package com.example.datebasetestdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 创建一个通知
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("important", "Important", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }
        notificationButton.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            val pi = PendingIntent.getActivity(this, 0, intent, 0)
            val notification = NotificationCompat.Builder(this, "important")
                    .setContentTitle("This is content title")
                    .setStyle(NotificationCompat.BigTextStyle().bigText("Learn how to build notification, send and sync data, and use voice action, Get the official Android IDF and developer tools to build apps"))
                    .setSmallIcon(R.mipmap.small_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.large_icon))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build()
            manager.notify(1, notification)
        }

        val dbHelper = MyDataBaseHelper(this, "BookStore.db", 1)
        createDataBase.setOnClickListener {
            dbHelper.writableDatabase
        }

        addData.setOnClickListener {
            val db = dbHelper.writableDatabase
            // 添加数据的第一种方式
            val value1 = ContentValues().apply {
                put("name", "jyf")
                put("author", "jyf")
                put("pages", 234)
                put("price", 16.96)
            }
            // 比较好的方式
            db.insert("Book", null, value1)
            db.execSQL("insert into Book(name, author, pages, price) values(?, ?, ?, ?)",
            arrayOf("Fage", "Jyf", "520", "56.47"))
        }

        queryData.setOnClickListener {
            val db = dbHelper.writableDatabase
            // 查询Book表中的所有数据
//            val cursor = db.query("Book", null, null, null, null, null, null)
//            if (cursor.moveToFirst()) {
//                do {
//                    // 遍历Cursor对象，取出数据并打印出来
//                    val name = cursor.getString(cursor.getColumnIndex("name"))
//                    val author = cursor.getString(cursor.getColumnIndex("author"))
//                    val pages = cursor.getInt(cursor.getColumnIndex("pages"))
//                    val price = cursor.getDouble(cursor.getColumnIndex("price"))
//                    Log.d(TAG, "name is $name author is $author pages is $pages price is $price")
//                } while (cursor.moveToNext())
//            }
//            cursor.close()

            // 第二种查询方式
            val cursor1 = db.rawQuery("select * from Book", null)
            if (cursor1.moveToFirst()) {
                do {
                    // 遍历Cursor对象，取出数据并打印出来
                    val name = cursor1.getString(cursor1.getColumnIndex("name"))
                    val author = cursor1.getString(cursor1.getColumnIndex("author"))
                    val pages = cursor1.getInt(cursor1.getColumnIndex("pages"))
                    val price = cursor1.getDouble(cursor1.getColumnIndex("price"))
                    Log.d(TAG, "name is $name author is $author pages is $pages price is $price")
                } while (cursor1.moveToNext())
            }
            cursor1.close()
        }

        updateData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val value = ContentValues()
            value.put("price", 25.67)
            db.update("Book", value, "name = ?", arrayOf("jyf"))

            // 第二种方式
            db.execSQL("update Book set price = ? where name = ?",
            arrayOf("45.67", "jyf"))
        }

        deleteData.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.delete("Book", "pages = ?", arrayOf("234"))

            // 第二种方式
            db.execSQL("delete from Book where pages > ?", arrayOf("500"))
        }

        // 事务
        replaceData.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.beginTransaction()
            try {
                db.delete("Book", null, null)
                val values = ContentValues().apply {
                    put("name", "First Code")
                    put("author", "jyf")
                    put("pages", 234)
                    put("price", 16.96)
                }
                db.insert("Book", null, values)
                db.setTransactionSuccessful()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                db.endTransaction()
            }
        }
    }
}