package com.example.sleepmonitor_master_v3

import android.util.Log
import org.junit.Test

import org.junit.Assert.*
import androidx.annotation.RequiresApi


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
 class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
     fun test3(){
        val myList= ArrayList<String?>()
        var file1:String?= "1"
        println("file1:"+file1.hashCode())
        myList.add(file1)
        file1=null
        myList.forEach {
            println("it:${it.hashCode()}")
            println(it==null)
        }
    }





    @Test
    fun test1(){
        var yAxisData =
            mutableListOf<Int>(5000,6000,4500,5210,6521)
                var max = yAxisData.maxOfOrNull { (it/60) }
        print("max" + max)
        var min = yAxisData.minOfOrNull { (it/60) }
        print("min" +min)

    }


    @Test
    fun test() {
        val binaryString = "0001"
        val intForString = binaryString.toInt(2)
        println(intForString)
        val answerMap = mutableMapOf<Int, Boolean>()
        val maxBitIwannaCheck = 3
        for (i in 0..maxBitIwannaCheck) {
            val standard = 1 shl i
            print((intForString and standard).toString()+"!!!!!!!!!")
            answerMap[i] = intForString and standard > 0
        }
        print(answerMap)
    }

    @Test
    fun getZeroAndOne() {
        val nowM = TimeUtil.getNowM()
        println(nowM +"@@@@@@@@@@@@@@")
        
    }

}