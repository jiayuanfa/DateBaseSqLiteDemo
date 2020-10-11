package com.example.datebasetestdemo

import kotlin.reflect.KProperty

fun <T> later(block: () -> T) = Later(block)

/**
 * 懒加载
 */
class Later<T>(val block: () -> T) {

    var value: Any? = null

    operator fun getValue(any: Any?, prop: KProperty<*>): T {
        if (value == null) {
            value = block()
        }
        return value as T
    }
}