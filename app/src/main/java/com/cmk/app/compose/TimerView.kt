package com.cmk.app.compose

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class Screen {
    Input, CountDown
}

@Composable
fun MyApp() {
    var timeInSec = 0
    Surface(color = MaterialTheme.colors.background) {
        val screen = remember { mutableStateOf(Screen.Input) }
        Crossfade(targetState = screen) {
            when (screen) {

            }
        }
    }
}

@Composable
fun InputScreen() {
    val input by remember { mutableStateOf(listOf<Int>()) }
    val hasCountdownValue by remember { mutableStateOf(input) }
    val (hou, min, sec) = remember {
        mutableListOf<Int>().run {
            repeat(6 - input.size) {
                add(0)
            }
            addAll(input)
            Triple(
                "${get(0)}${get(1)}",
                "${get(2)}${get(3)}",
                "${get(4)}${get(5)}"
            )
        }
    }
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(start = 30.dp, end = 30.dp)
        ) {
            listOf(hou to "h", min to "m", sec to "s")
        }
    }
}