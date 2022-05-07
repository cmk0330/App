package com.cmk.app.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimateAsStateDemo() {
    val blue = remember { mutableStateOf(true) }


    val color = animateColorAsState(
        if (blue.value) Color.Blue else Color.Red,
        animationSpec = spring(Spring.StiffnessVeryLow),
        finishedListener = { blue.value = !blue.value }
    )


    Column(Modifier.padding(8.dp)) {
        Text(text = "AnimateAsStateDemo")
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Button(onClick = { blue.value = !blue.value }) {
            Text(text = "Change Color")
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Box(
            Modifier
                .background(color.value)
                .size(128.dp)
        )
    }
}

@Composable
fun UpdateTransitionDemo() {
    var boxState = remember { mutableStateOf(BoxState.Small) }
    val transition = updateTransition(targetState = boxState, label = "")

//    Column(Modifier.padding(16.dp)) {
//        Text("UpdateTransitionDemo")
//        Spacer(Modifier.height(16.dp))
//
//        val color = transition.animateColor(label = "") { boxState.value.color }
//        val size = transition.animateDp(label = "", transitionSpec = {
//            if (targetState.value == BoxState.Large) {
//                spring(stiffness = Spring.StiffnessVeryLow)
//            } else {
//                spring(stiffness = Spring.StiffnessHigh)
//            }
//        }) {
//            boxState.value.size
//        }
//
//        Button(
//            onClick = { boxState.value = !boxState.value }
//        ) {
//            Text("Change Color and size")
//        }
//        Spacer(Modifier.height(16.dp))
//        Box(
//            Modifier
//                .size(size.value)
//                .background(color.value)
//        )
//    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimateVisibilityDemo() {
    var visible = remember { mutableStateOf(true) }

    Column(Modifier.padding(16.dp)) {
        Text("AnimateVisibilityDemo")
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { visible.value = !visible.value }
        ) {
            Text(text = if (visible.value) "Hide" else "Show")
        }

        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(visible.value) {
            Box(
                Modifier
                    .size(128.dp)
                    .background(Blue)
            )
        }
    }
}

private sealed class BoxState(val color: Color, val size: Dp) {
    operator fun not() = if (this is Small) Large else Small

    object Small : BoxState(Blue, 64.dp)
    object Large : BoxState(Red, 128.dp)
}