package com.example.datebasetestdemo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

/**
 * 数据库类
 * 每次升级需要判断版本号来增量添加
 */
class MyDataBaseHelper(private val context: Context, name: String, version: Int)
    : SQLiteOpenHelper(context, name, null, version) {

    private val createBook = "create table Book (" +
    "id integer primary key autoincrement, " +
            "author text, " +
            "price real, " +
            "pages integer, " +
            "name text," +
    "category_id integer)"

    private val createCategory = "create table Book (" +
            "id integer primary key autoincrement, " +
            "category_name text, " +
            "category_code integer)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createBook)
        db.execSQL(createCategory)
        Toast.makeText(context, "Created succeeded", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 第二版已经存在了Book表，所以只新增Category表即可
        if (oldVersion <= 1) {
            db.execSQL(createCategory)
        }
        // 第二个版本 只需要在表中新增字段即可
        if (oldVersion <= 2) {
            db.execSQL("alter table Book add column category_id integer")
        }
        onCreate(db)
    }
}