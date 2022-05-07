package com.cmk.app.test

import com.cmk.app.net.api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * @Author: romens
 * @Date: 2019-11-6 14:57
 * @Desc: kotlin学习
 */
class LearnKotlin {

    lateinit var callBack: KCallBack

    /**
     * channel用于协程间的通信
     */
    fun channel() = runBlocking {
        val channel = Channel<Int>()
        launch {
            for (x in 1..5) {
                channel.send(x * x)
            }
            println("接受的--${channel.receive()}")
        }
    }

    fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
        for (x in 1..5) send(x * x)
    }

    fun CoroutineScope.produceSquares1(): ReceiveChannel<Int> {
        return produce {
            for (x in 1..5) send(x * x)
        }
    }

    fun channel1() = runBlocking {
        launch {
            val produceSquares = produceSquares()
            produceSquares.consumeEach { println(it) }
        }
    }
    /*--------------------------------------------------------------------------------------------*/

    /**
     * lambda
     */
    //入参是函数的高阶函数
    fun fooIn(func: () -> Int, action: () -> Unit) {
        println("fooIn")
        func // 也可以去掉()
        action
    }

    //出参是函数的高阶函数
    fun fooOut(): () -> Unit {
        println("fooOut")

        return { println("hello") }
    }

    fun addTest(a: Int, func: (Int) -> Boolean): Int {
        return if (func(a)) {
            a
        } else 0
    }


    fun invokeAddTest() {
        println(addTest(10, {it>5}))
        println(addTest(0) { it > 5 })
        // print--->10 ,0
    }

    private inline fun sum(abc: (Char) -> Int): Int {
        var sum: Int = 0
        for (e in this) {
            sum += abc(e)
        }
        return sum
    }

    private operator fun iterator(): Iterator<Char> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun sele(s: Int, b: String): Int {
        return s
    }

    private fun call(back: KCallBack) {
    }

    fun back() {

        fooIn({ 1 }) {

        }

        call(object : KCallBack {
            override fun vCallBack(a: String, b: String) = Unit

            override fun sCallBack(a: String, b: String): String = Unit.toString()
        })

        val str = "abcd"
        str.sumBy { it.toInt() }
    }

    //正常
    private fun sum1(a: Int, b: Int): Int {
        2.iop(3)
        lunch {
            a
        }
        prin("aa")
        return a + b
    }

    fun lunch(a: Int.() -> Int) {
        io(3.a())
    }

    private fun io(b: Int) {
        println(b)
    }

    //lambda
    val sumL: (Int, Int) -> Int = { a, b -> a + b }

    //或者
    val sumL1 = { a: Int, b: Int -> a + b }

    val te = fun(a: Int, b: Int): Int = a + b

    val iop = fun Int.(other: Int): Int = this + other

    val prin = { str: String -> println(str) }

    fun foo(a: String, b: (String) -> Unit) {
        b(a)
    }

    fun tt(a: Int): () -> Int {
        var b = 3
        return fun(): Int {
            b++
            return a + b
        }
    }

    fun add(a: Int, b: Int): Int {
        return a + b
    }

    val ad = fun(a: Int, b: Int): Int = a + b
    /*--------------------------------------------------------------------------------------------*/

    /**
     * 委托模式
     */
    /*-------类委托------*/
    // 约束类,约束是接口或者抽象类，它定义了通用的业务类型，也就是需要被代理的业务
    interface IGamePlayer {
        //打排位赛
        fun rank()

        //升级
        fun upGrade()
    }

    //委托对象,负责对真是角色的应用，将约束累定义的业务委托给具体的委托对象
    class DelegateGamePlayer(private val player: IGamePlayer) : IGamePlayer by player

    //被委托对象，具体的业务逻辑执行者,游戏中的代练
    class RealGamePlayer(private var name: String) : IGamePlayer {
        override fun rank() {
            println("$name 开始打排位")
        }

        override fun upGrade() {
            println("$name 升级了")
        }
    }

    // 类DelegateGamePlayer实现接口IGamePlayer,并将所有共有的方法委托给一个指定的对象
    // 也就是说把类DelegateGamePlayer因继承而需要实现的方法委托给一个对象完成，从而不需要在该类内显式的实现
    // 如果DelegateGamePlayer没有写 'by player'就必须重写IGamePlayer的方法
    fun main() {
        val realGamePlayer = RealGamePlayer("张三")
        val delegateGamePlayer = DelegateGamePlayer(realGamePlayer)
        delegateGamePlayer.rank()
        delegateGamePlayer.upGrade()
    }

    /*--------委托属性---------*/
    class Test {
        //属性委托
        var prop: String by Delegate()
        var observableProp: String by Delegates.observable("xxx默认值") { property, oldValue, newValue ->
            println("property:$property:$oldValue->$newValue")
        }
        var vetoableProp: Int by Delegates.vetoable(0) { property: KProperty<*>, oldValue: Int, newValue: Int ->
            newValue > oldValue
        }

        class User(val map: Map<String, Any?>) {
            val name: String by map
            val age: Int by map
        }

        // 测试
        fun observable() {
            observableProp = "第一次修改值"
            observableProp = "第二次修改值"
            // 结果
            //property: var observableProp: kotlin.String: 默认值：xxx -> 第一次修改值
            //property: var observableProp: kotlin.String: 第一次修改值 -> 第二次修改值
        }

        fun vetoable() {
            println("vetoableProp=$vetoableProp")
            vetoableProp = 10
            println("vetoableProp=$vetoableProp")
            vetoableProp = 5
            println("vetoableProp=$vetoableProp")
            vetoableProp = 100
            println("vetoableProp=$vetoableProp")
            //结果
//            vetoableProp=0
//            0 -> 10
//            vetoableProp=10
//            10 -> 5
//            vetoableProp=10
//            10 -> 100
//            vetoableProp=100
        }

        fun user() {
            val user = User(
                mapOf(
                    "name" to "明天你好",
                    "age" to 99
                )
            )
            println("name=${user.name} age=${user.age}")
            //结果
//            name=明天你好 age=99
        }
    }

    /**
     * thisRef —— 必须与 属性所有者 类型（对于扩展属性——指被扩展的类型）相同或者是它的超类型；
     * property —— 必须是类型 KProperty<*>或其超类型。
     * value —— 必须与属性同类型或者是它的子类型。
     */
    class Delegate {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return "$thisRef, thank you for delegating '${property.name}' to me!"
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            println("$value has been assigned to '${property.name}' in $thisRef.")
        }
    }
}