package com.example.datebasetestdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * 泛型
 */
class ParamTActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun useT() {
        val myClass = MyClass<Int>()
        myClass.method(123)
        myClass.method2(234)
    }

    /**
     * 懒加载
     */
    val urlMatcher by later {
        // 初始化代码即可
    }
}