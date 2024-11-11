package com.mhss.app.mybrain.presentation.timer

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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
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
import com.mhss.app.mybrain.ui.theme.Black
import com.mhss.app.mybrain.ui.theme.Green
import com.mhss.app.mybrain.ui.theme.MyBrainTheme
import com.mhss.app.mybrain.ui.theme.Red
import com.mhss.app.mybrain.util.timer.checkNumberPicker
import com.mhss.app.mybrain.util.timer.ClockButton
import com.mhss.app.mybrain.util.timer.NumberPicker
import com.mhss.app.mybrain.util.timer.parseInt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue

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
                .imePadding().background(Black),
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
                    modifier = Modifier.size(350.dp),
                    timeText = timerState.timeText,
                    progress = timerState.progress,
                )
            }

            Buttons(
                modifier = Modifier
                    .align(BottomCenter)
                    .padding(dimensionResource(id = R.dimen._7sdp)),
                timerState = timerState,
                timerActions = timerActions,
                isDoneTransition = isDoneTransition,
            )
        }
    }
}

@Composable
fun SpiralIndicator(
    progress: Float,  // Progress value between 0 and 1
    modifier: Modifier = Modifier,
    strokeWidth: Float,
    color: Color = Color.Green
) {
    val canvasSize = 300.dp  // Set the canvas size

    // Animate the rotation smoothly
    val animatedRotation by animateFloatAsState(
//        targetValue = 360f * progress,
        targetValue = progress * 30f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        )
    )

    // Animate the number of segments to be drawn smoothly
    val animatedProgress by animateFloatAsState(
//        targetValue = progress,
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        )
    )

    Canvas(
        modifier = modifier
            .size(canvasSize)
            .graphicsLayer {
                rotationZ += animatedRotation  // Apply animated rotation
            }
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Define max step size for the spiral (ensure it fits inside the canvas)
        val maxStep = minOf(size.width, size.height) / 5

        // Path for the square spiral
        val path = Path().apply {
            moveTo(centerX, centerY)

            var step = 100f  // Initial step size
            var currentX = centerX
            var currentY = centerY
            var direction = 0

            // Loop to create segments outward in a square spiral pattern
            val segmentsToDraw = (animatedProgress * 10).toInt()  // Control the number of steps based on animated progress
            for (i in 0 until segmentsToDraw) {  // Control the number of steps being drawn gradually
                when (direction % 4) {
                    0 -> currentX = (currentX + step).coerceAtMost(size.width)
                    1 -> currentY = (currentY + step).coerceAtMost(size.height)
                    2 -> currentX = (currentX - step).coerceAtLeast(0f)
                    3 -> currentY = (currentY - step).coerceAtLeast(0f)
                }

                lineTo(currentX, currentY)

                // Increase the step size every 2 iterations to make the spiral expand
                if (i % 2 == 1) {
                    step += maxStep
                }

                direction++
            }
        }

        // Draw the square spiral path with the animated progress
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
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
    Column(
        modifier = modifier
            .wrapContentHeight()  // Use only one size modifier to prevent conflicts
            .fillMaxWidth(),  // Fill the width, adjust based on your layout needs
        horizontalAlignment = Alignment.CenterHorizontally  // Center items horizontally
    ) {
        // Spiral Indicator at the top
        SpiralIndicator(
            progress = progress,
            modifier = Modifier.size(250.dp),  // Ensure a consistent size for the spiral
            strokeWidth = 50f,
            color = Color.Green,
        )

        Spacer(modifier = Modifier.height(50.dp))  // You can increase this value to adjust spacing

        // Text below the spiral
        Text(
            modifier = Modifier,
            text = timeText,
            style = MaterialTheme.typography.h3,
            fontWeight = FontWeight.Light,
            color = Color.White  // Adjust color for readability
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
    Box(modifier = modifier.padding(bottom = 50.dp)) {
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

@Preview(device = Devices.TABLET, widthDp = 768, heightDp = 1024)
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
