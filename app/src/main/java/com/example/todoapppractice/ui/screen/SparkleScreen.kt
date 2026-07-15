package com.example.todoapppractice.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SparkleScreen() {
    val positionList = remember{mutableStateListOf<Int>(0)}
    val visibleStateList by RandomUtil.visibleState.collectAsStateWithLifecycle()
    val x by RandomUtil.xOffset.collectAsStateWithLifecycle()
    val y by RandomUtil.yOffset.collectAsStateWithLifecycle()
    Box(contentAlignment = Alignment.Center, modifier =  Modifier.fillMaxSize()) {
        for (i in 0..4) {
            AnimatedVisibility(visible = visibleStateList[i]) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .offset(x = x[i].dp, y = y[i].dp)
                        .background(color = Color.Red)
                )
            }
        }

    }
}

object RandomUtil {
    val visibleState = MutableStateFlow(mutableListOf(true, true, true, true, true))
    val xOffset = MutableStateFlow(mutableListOf(0, 0, 0, 0, 0))
    val yOffset = MutableStateFlow(mutableListOf(0, 0, 0, 0, 0))

    suspend fun randomize() {
        while (true) {
            val delayTime: Long = Random(1).nextLong(2000)
            delay(delayTime)

            val newXOffset = mutableListOf<Int>()
            val newYOffset = mutableListOf<Int>()
            val newVisibleStateList = mutableListOf<Boolean>()
            for (i in 0..4) {
                newVisibleStateList.add(!visibleState.value[0])
                newXOffset.add(Random(System.currentTimeMillis()).nextInt(50))
                newYOffset.add(Random(System.currentTimeMillis()).nextInt(50))
            }
            visibleState.update {
                newVisibleStateList
            }
            xOffset.update {
                newXOffset
            }
            yOffset.update {
                newYOffset
            }
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            randomize()
        }
    }

}
class A(val b:Int){
   fun printf(){
       print(b)
   }
    init{
        print(b)
    }
}
