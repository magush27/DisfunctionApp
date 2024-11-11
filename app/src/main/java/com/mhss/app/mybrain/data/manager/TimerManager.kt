package com.mhss.app.mybrain.data.manager


import com.mhss.app.mybrain.domain.model.TimerState
import com.mhss.app.mybrain.util.timer.GlobalProperties.TIME_FORMAT
import com.mhss.app.mybrain.data.workManager.worker.TIMER_RUNNING_TAG
import com.mhss.app.mybrain.data.workManager.worker.TimerRunningWorker
import com.mhss.app.mybrain.util.timer.CountDownTimerHelper
import com.zhuinden.flowcombinetuplekt.combineTuple
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.random.Random

@Singleton
class TimerManager @Inject constructor(
    private val workRequestManager: WorkRequestManager,
) {

    // Definir rangos para tiempo aleatorio
//    private val minHours = 0
//    private val maxHours = 24
//
//    private val minMinutes = 0
//    private val maxMinutes = 59
//
//    private val minSeconds = 0
//    private val maxSeconds = 59

    private val timeInMillisFlow = MutableStateFlow(0L)
    private val timeTextFlow = MutableStateFlow("00:00:00")
    private val hourFlow = MutableStateFlow(0)
    private val minuteFlow = MutableStateFlow(0)
    private val secondFlow = MutableStateFlow(0)
    private val progressFlow = MutableStateFlow(0f)
    private val isPlayingFlow = MutableStateFlow(false)
    private val isDoneFlow = MutableStateFlow(true)

    val timerState = combineTuple(
        timeInMillisFlow,
        timeTextFlow,
        hourFlow,
        minuteFlow,
        secondFlow,
        progressFlow,
        isPlayingFlow,
        isDoneFlow,
    ).map { (timeInMillis, time, hour, minute, second, progress, isPlaying, isDone) ->
        TimerState(
            timeInMillis = timeInMillis,
            hour = hour,
            minute = minute,
            second = second,
            timeText = time,
            progress = progress,
            isPlaying = isPlaying,
            isDone = isDone,
        )
    }

    private var countDownTimerHelper: CountDownTimerHelper? = null

    fun setTHour(hour: Int) {
        hourFlow.value = hour
    }

    fun setMinute(minute: Int) {
        minuteFlow.value = minute
    }

    fun setSecond(second: Int) {
        secondFlow.value = second
    }

    // Función para generar tiempo aleatorio
//    private fun generateRandomTime(): Long {
//        val randomHours = (minHours..maxHours).random()
//        val randomMinutes = (minMinutes..maxMinutes).random()
//        val randomSeconds = (minSeconds..maxSeconds).random()
//        return (randomHours.hours + randomMinutes.minutes + randomSeconds.seconds).inWholeMilliseconds
//    }

    fun setCountDownTimer() {
        timeInMillisFlow.value =
            (hourFlow.value.hours + minuteFlow.value.minutes + secondFlow.value.seconds).inWholeMilliseconds

        countDownTimerHelper = object : CountDownTimerHelper(timeInMillisFlow.value, 1000) {
            private val coroutineScope = CoroutineScope(Dispatchers.Default)

            override fun onTimerTick(millisUntilFinished: Long) {
                coroutineScope.launch {
                    // Calculate progress based on remaining time
                    var progressValue = millisUntilFinished.toFloat() / timeInMillisFlow.value
                    handleTimerValues(true, millisUntilFinished.formatTime(), progressValue)

                    // Scale the random fluctuation based on the remaining time
                    val fluctuationRange = calculateFluctuationRange(millisUntilFinished)

                    // Apply subtle random fluctuation within the calculated range
                    val randomAdjustment = Random.nextLong(-fluctuationRange, fluctuationRange)
                    val adjustedTime = (millisUntilFinished + randomAdjustment).coerceAtLeast(1000L)

                    // Update progress based on the new adjusted time
                    progressValue = adjustedTime.toFloat() / timeInMillisFlow.value
                    handleTimerValues(true, adjustedTime.formatTime(), progressValue)

                    delay(1000L) // Wait 3 seconds before making another adjustment
                }
            }

            override fun onTimerFinish() {
                // When the timer finishes, restart it
                setCountDownTimer()
                start()
                countDownTimerHelper?.start()
            }
        }
    }

    private fun calculateFluctuationRange(millisUntilFinished: Long): Long {
        return when {
            millisUntilFinished < 60_000 -> 3000L  // 1 second fluctuation for times under 1 minute
            millisUntilFinished < 3600_000 -> 100000L  // 5 seconds fluctuation for times under 1 hour
            millisUntilFinished < 24 * 3600_000 -> 30_000L  // 30 seconds fluctuation for times under 1 day
            else -> 60_000L  // 1 minute fluctuation for times above 1 day
        }
    }

    fun pause() {
        countDownTimerHelper?.pause()
        isPlayingFlow.value = false
    }

    fun reset() {
        countDownTimerHelper?.restart()
        handleTimerValues(false, timeInMillisFlow.value.formatTime(), 0f)
        isDoneFlow.value = true
        workRequestManager.cancelWorker(TIMER_RUNNING_TAG)
    }

    fun start() {
        countDownTimerHelper?.start()
        isPlayingFlow.value = true
        if (isDoneFlow.value) {
            progressFlow.value = 1f
            workRequestManager.enqueueWorker<TimerRunningWorker>(TIMER_RUNNING_TAG)
            isDoneFlow.value = false
        }
    }

    private fun handleTimerValues(
        isPlaying: Boolean,
        text: String,
        progress: Float,
    ) {
        isPlayingFlow.value = isPlaying
        timeTextFlow.value = text
        progressFlow.value = progress
    }

    fun Long.formatTime(): String = String.format(
        TIME_FORMAT,
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) % 60,
        TimeUnit.MILLISECONDS.toSeconds(this) % 60,
    )
}