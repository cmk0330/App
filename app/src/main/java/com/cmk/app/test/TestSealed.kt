package com.cmk.app.test

sealed class TestSealed {
    class Add(val value: Int) : TestSealed()
    class Substract(val value: Int) : TestSealed()
    class Multiply(val value: Int) : TestSealed()
    class Divide(val value: Int) : TestSealed()
    /*---------------*/
    object Increment : TestSealed()

    object Decrement : TestSealed()

    fun execute(x: Int, op: TestSealed) = when (op) {
        is TestSealed.Add -> x + op.value
        is Substract -> x - op.value
        is Multiply -> x * op.value
        is Divide -> x / op.value
        is Increment -> x + 1
        is Decrement -> x - 1
    }
}