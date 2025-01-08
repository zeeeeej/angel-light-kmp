package com.yunext.angel.light.ui.compoent

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

private const val GESTURE_ACCEPTED_START = 130f
private const val GESTURE_MINIMUM_FINISH = 300f

@Composable
fun IosBackGestureHandler(
    isEnabled: Boolean,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetStart by remember { mutableStateOf(-1f) }
    var offsetFinish by remember { mutableStateOf(-1f) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        if (offset.x <= GESTURE_ACCEPTED_START) offsetStart = offset.x
                    },

                    onDragEnd = {
                        val isOnBackGestureActivated = isEnabled
                                && offsetStart in 0f..GESTURE_ACCEPTED_START
                                && offsetFinish > GESTURE_MINIMUM_FINISH

                        if (isOnBackGestureActivated) onBack()

                        offsetStart = -1f
                        offsetFinish = -1f
                    },

                    onHorizontalDrag = { change, _ -> offsetFinish = change.position.x }
                )
            }
    ) {
        content()
    }
}