package com.mhss.app.mybrain.presentation.timer

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mhss.app.mybrain.R
import com.mhss.app.mybrain.domain.model.TimerState
import com.mhss.app.mybrain.presentation.util.Screen
import com.mhss.app.mybrain.ui.theme.Black
import com.mhss.app.mybrain.ui.theme.Green
import com.mhss.app.mybrain.ui.theme.MyBrainTheme
import com.mhss.app.mybrain.ui.theme.Red
import com.mhss.app.mybrain.util.timer.checkNumberPicker
import com.mhss.app.mybrain.util.timer.BackgroundIndicator
import com.mhss.app.mybrain.util.timer.ClockAppBar
import com.mhss.app.mybrain.util.timer.ClockButton
import com.mhss.app.mybrain.util.timer.NumberPicker
import com.mhss.app.mybrain.util.timer.parseInt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(
    ExperimentalAnimationApi::class,
)
@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    timerState: TimerState,
    timerActions: TimerActions,
    navController: NavHostController
) {
    val isDoneTransition =
        updateTransition(timerState.isDone, label = stringResource(id = R.string.complete))

    Surface(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
        ) {
            val timerPickerPaddingStart =
                if (maxWidth > 400.dp) dimensionResource(id = R.dimen._30sdp) else 0.dp

            TimerAppBar(modifier = Modifier.statusBarsPadding(), navController = navController)

            isDoneTransition.AnimatedVisibility(
                visible = { isTimerDone -> isTimerDone },
                enter = scaleIn(
                    animationSpec = tween(
                        durationMillis = 1,
                        easing = FastOutLinearInEasing,
                    ),
                ),
                exit = fadeOut(),
            ) {
                TimerPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = maxHeight / 3,
                            start = timerPickerPaddingStart,
                        ),
                    timerActions = timerActions,
                    timeText = timerState.timeText,
                )
            }

            isDoneTransition.AnimatedVisibility(
                visible = { isTimerDone -> !isTimerDone },
                modifier = Modifier.align(Center),
                enter = fadeIn(),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = 1,
                        easing = FastOutLinearInEasing,
                    ),
                ),
            ) {
                Timer(
                    modifier = Modifier.size(dimensionResource(id = R.dimen._268sdp)),
                    timeText = timerState.timeText,
                    progress = timerState.progress,
                )
            }

            Buttons(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(dimensionResource(id = R.dimen._7sdp)),
                timerState = timerState,
                timerActions = timerActions,
                isDoneTransition = isDoneTransition,
            )
        }
    }
}


//@Composable
//fun SpiralIndicator(
//    progress: Float,
//    modifier: Modifier = Modifier,
//    strokeWidthDp: Float = 6f,  // Use dp value here, which we'll convert to px
//    spiralColor: Color = Color.Green,
//    shadowColor: Color = Color.Gray,
//    shadowOffset: Float = 10f
//) {
//
//    Canvas(
//        modifier = modifier
//            // Apply rotation based on progress
//            .graphicsLayer {
//                // Progress ranges from 0 to 1, map it to 0 to 360 degrees
//                rotationZ = 360f * progress
//            }
//    ) {
//        // Get the width and height of the canvas
//        val canvasWidth = size.width
//        val canvasHeight = size.height
//
//        // Parameters for the spiral
//        val centerX = canvasWidth / 2
//        val centerY = canvasHeight / 2
//        val maxRadius = min(canvasWidth, canvasHeight) / 2 * 0.8f  // Control the spiral size
//
//        // Spiral equation: r = a + b * θ (polar coordinates)
//        val a = 5f
//        val b = 15f
//
//        // Path to draw the spiral
//        val path = Path().apply {
//            moveTo(centerX, centerY)
//
//            // Draw a spiral based on progress
//            for (angle in 0 until (360 * 5)) {  // 5 turns of spiral
//                val theta = Math.toRadians(angle.toDouble())
//                val radius = a + b * theta
//
//                val x = centerX + radius * cos(theta).toFloat()
//                val y = centerY + radius * sin(theta).toFloat()
//
//                if (angle == 0) {
//                    moveTo(x.toFloat(), y.toFloat())
//                } else {
//                    lineTo(x.toFloat(), y.toFloat())
//                }
//
//                // Stop drawing when the progress is met
//                if (angle >= (progress * 360 * 5).toInt()) break
//            }
//        }
//
//        // Draw shadow
//        drawPath(
//            path = path,
//            color = shadowColor,
//            style = Stroke(width = 6f, cap = StrokeCap.Round),  // Slightly thicker for shadow
//        )
//
//        // Draw the spiral
//        drawPath(
//            path = path,
//            color = spiralColor,
//            style = Stroke(width = 6f, cap = StrokeCap.Round),
//        )
//    }
//}

@Composable
fun SpiralIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidthDp: Float = 6f,
    spiralColor: Color = Color.Green,
    shadowColor: Color = Color.Green.copy(alpha = 0.3f),  // Add transparency for a softer shadow effect
    shadowOffset: Float = 10f
) {
    val density = LocalDensity.current

    // Convert dp values to pixels
    val strokeWidthPx = with(density) { strokeWidthDp.dp.toPx() }
    val shadowOffsetPx = with(density) { shadowOffset.dp.toPx() }  // Convert shadow offset to px

    Canvas(
        modifier = modifier
            .graphicsLayer {
                rotationZ = 360f * progress
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Center of the canvas
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        // Spiral parameters
        val a = 5f
        val b = 15f

        // Path for the shadow, offset by `shadowOffsetPx`
        val shadowPath = Path().apply {
            moveTo(centerX + shadowOffsetPx, centerY + shadowOffsetPx)

            for (angle in 0 until (360 * 5)) {  // Draw 5 turns of the spiral
                val theta = Math.toRadians(angle.toDouble())
                val radius = a + b * theta

                val x = centerX + radius * cos(theta).toFloat() + shadowOffsetPx
                val y = centerY + radius * sin(theta).toFloat() + shadowOffsetPx

                if (angle == 0) {
                    moveTo(x.toFloat(), y.toFloat())
                } else {
                    lineTo(x.toFloat(), y.toFloat())
                }

                if (angle >= (progress * 360 * 5).toInt()) break
            }
        }

        // Draw shadow path
        drawPath(
            path = shadowPath,
            color = shadowColor,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )

        // Path for the main spiral
        val mainPath = Path().apply {
            moveTo(centerX, centerY)

            for (angle in 0 until (360 * 5)) {
                val theta = Math.toRadians(angle.toDouble())
                val radius = a + b * theta

                val x = centerX + radius * cos(theta).toFloat()
                val y = centerY + radius * sin(theta).toFloat()

                if (angle == 0) {
                    moveTo(x.toFloat(), y.toFloat())
                } else {
                    lineTo(x.toFloat(), y.toFloat())
                }

                if (angle >= (progress * 360 * 5).toInt()) break
            }
        }

        // Draw the main spiral path
        drawPath(
            path = mainPath,
            color = spiralColor,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )
    }
}


@Composable
private fun TimerAppBar(modifier: Modifier = Modifier, navController: NavHostController) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.timer),
                color = Black,
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Black
                )
            }
        },
        backgroundColor = Green,
        elevation = 0.dp,
    )
}

@Composable
private fun TimerPicker(
    modifier: Modifier = Modifier,
    timerActions: TimerActions,
    timeText: String,
) {
    Row(
        modifier = modifier,
    ) {
        val textStyle = MaterialTheme.typography.h3
        var hour by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(timeText.substringBefore(":")))
        }
        var minute by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(
                TextFieldValue(timeText.substringAfter(":").substringBefore(':')),
            )
        }
        var second by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(timeText.substringAfterLast(":")))
        }

        NumberPicker(
            modifier = Modifier.weight(1f),
            number = hour,
            timeUnit = stringResource(id = R.string.hours),
            onNumberChange = { value ->
                if (value.text.checkNumberPicker(maxNumber = 99)) {
                    hour = value
                    timerActions.setHour(hour.text.parseInt())
                    timerActions.setCountDownTimer()
                }
            },
        )

        Text(
            modifier = Modifier.padding(top = 17.dp),
            text = ":",
            style = textStyle,
        )

        NumberPicker(
            modifier = Modifier.weight(1f),
            number = minute,
            timeUnit = stringResource(id = R.string.minutes),
            onNumberChange = { value ->
                if (value.text.checkNumberPicker(maxNumber = 59)) {
                    minute = value
                    timerActions.setMinute(minute.text.parseInt())
                    timerActions.setCountDownTimer()
                }
            },
        )

        Text(
            modifier = Modifier.padding(top = 17.dp),
            text = ":",
            style = textStyle,
        )

        NumberPicker(
            modifier = Modifier.weight(1f),
            number = second,
            timeUnit = stringResource(id = R.string.seconds),
            onNumberChange = { value ->
                if (value.text.checkNumberPicker(59)) {
                    second = value
                    timerActions.setSecond(second.text.parseInt())
                    timerActions.setCountDownTimer()
                }
            },
        )
    }
}

@Composable
private fun Timer(
    modifier: Modifier = Modifier,
    timeText: String,
    progress: Float,
) {
    Box(modifier = modifier) {
        SpiralIndicator(
            progress = progress,
            modifier = modifier
                .fillMaxSize()
                .scale(scaleX = 1f, scaleY = 1f),
            strokeWidthDp = 6f,
            spiralColor = Color.Green,
           // shadowColor = Color.Black.copy(alpha = 0.3f),  // Adjust shadow transparency
            shadowOffset = 10f
        )
        Text(
            modifier = Modifier.align(Center).background(Black),
            text = timeText,
            style = MaterialTheme.typography.h3,
            fontWeight = FontWeight.Light,
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    timerState: TimerState,
    timerActions: TimerActions,
    isDoneTransition: Transition<Boolean>,
) {
    Box(modifier = modifier.padding(bottom = 30.dp)) {
        isDoneTransition.AnimatedVisibility(
            visible = { isTimerDone -> isTimerDone },
            enter = expandHorizontally(
                animationSpec = tween(
                    durationMillis = 1,
                    easing = FastOutLinearInEasing,
                ),
            ),
            exit = shrinkHorizontally(
                animationSpec = tween(
                    durationMillis = 1,
                    easing = FastOutLinearInEasing,
                ),
            ),
        ) {
            ClockButton(
                painter = painterResource(R.drawable.ic_play),
                contentDescription = stringResource(R.string.start),
                onClick = { timerActions.start() },
                enabled = timerState.timeInMillis != 0L,
                )
        }

        isDoneTransition.AnimatedVisibility(
            visible = { isTimerDone -> !isTimerDone },
            enter = expandHorizontally(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing,
                ),
            ),
            exit = shrinkHorizontally(
                animationSpec = tween(
                    durationMillis = 1,
                    easing = FastOutLinearInEasing,
                ),
            ),
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                if (timerState.isPlaying) {
                    ClockButton(
                        painter = painterResource(R.drawable.ic_pause),
                        contentDescription = stringResource(R.string.pause),
                        onClick = { timerActions.pause() },
                        color = Red,
                    )
                } else {
                    ClockButton(
                        painter = painterResource(R.drawable.ic_play),
                        contentDescription = stringResource(R.string.resume),
                        color = Red,
                        onClick = { timerActions.start() },
                    )
                }
                ClockButton(
                    onClick = { timerActions.reset() },
                    painter = painterResource(R.drawable.ic_stop),
                    contentDescription = stringResource(R.string.cancel),
                    color = MaterialTheme.colors.onSurface,
                )
            }
        }
    }
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun TimerScreenPreview() {
    MyBrainTheme {
        TimerScreen(
            timerState = TimerState(),
            timerActions = object : TimerActions {},
            navController = rememberNavController()
        )
    }
}

@Preview(device = Devices.TABLET, uiMode = ORIENTATION_PORTRAIT, widthDp = 768, heightDp = 1024)
@Composable
private fun TimerScreenDarkPreview() {
    MyBrainTheme(darkTheme = true) {
        TimerScreen(
            timerState = TimerState(isDone = false, timeText = "00:00:10"),
            timerActions = object : TimerActions {},
            navController = rememberNavController()
        )
    }
}

