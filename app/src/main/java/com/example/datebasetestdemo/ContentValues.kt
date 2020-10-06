package com.example.datebasetestdemo

import android.content.ContentValues

/**
 * vararg 关键字指的是可以传入过个Pari类型的参数
 * 1：创建ContentValues对象
 * 2：遍历Pairs
 * 3: 取出数据并填入
 * 4： 使用when语句一一判断并覆盖所有类型
 */
fun cvOf(vararg pairs: Pair<String, Any?>) : ContentValues {
    val cv = ContentValues()
    for (pair in pairs) {
        val key = pair.first
        when (val value = pair.second) {
            is Int -> cv.put(key, value)
            is Long -> cv.put(key, value)
            is Short -> cv.put(key, value)
            is Float -> cv.put(key, value)
            is Double -> cv.put(key, value)
            is Boolean -> cv.put(key, value)
            is String -> cv.put(key, value)
            is Byte -> cv.put(key, value)
            is ByteArray -> cv.put(key, value)
            null -> cv.putNull(key)
        }
    }
    return cv
}