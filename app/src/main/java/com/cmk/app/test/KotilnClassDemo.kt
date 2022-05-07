package com.cmk.app.test

data class KotilnClassDemo (var a:Int,var b:Int,val c:Int){

    private var str1:String = ""
    private var str2:String= ""
    constructor(str1:String, str2:String) : this(1,2,3){
        println("------>constructor$str1====$str2")
        this.str1 = str1
        this.str2 = str2
    }

    init {
        println("------>$a+$b+$c")
    }

    init {
        println("------>init$str1====$str2")
    }


}