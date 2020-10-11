package com.example.datebasetestdemo

/**
 * 泛型类
 * 1：类的泛型指定为Int类型
 * 2：也可以写在方法前面
 * */
class MyClass<T> {
    fun method(param: T): T {
        return param
    }

    /**
     * 对类型进行限制
     */
    fun <T : Number> method2(param: T): T {
        return param
    }

    /**
     * 可空类型
     */
    fun <T : Any?> method3(param: T): T {
        return param
    }
}