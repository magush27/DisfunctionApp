package com.mhss.app.mybrain.data.manager
//
//import com.mhss.app.mybrain.domain.model.TimerState
//import com.mhss.app.mybrain.util.timer.GlobalProperties.TIME_FORMAT
//import com.mhss.app.mybrain.data.workManager.worker.TIMER_COMPLETED_TAG
//import com.mhss.app.mybrain.data.workManager.worker.TIMER_RUNNING_TAG
//import com.mhss.app.mybrain.data.workManager.worker.TimerCompletedWorker
//import com.mhss.app.mybrain.data.workManager.worker.TimerRunningWorker
//import com.mhss.app.mybrain.util.timer.CountDownTimerHelper
//import com.zhuinden.flowcombinetuplekt.combineTuple
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.map
//import java.util.concurrent.TimeUnit
//import javax.inject.Inject
//import javax.inject.Singleton
//import kotlin.time.Duration.Companion.hours
//import kotlin.time.Duration.Companion.minutes
//import kotlin.time.Duration.Companion.seconds
//
//@Singleton
//class TimerManager @Inject constructor(
//    private val workRequestManager: WorkRequestManager,
//) {
//
//    private val timeInMillisFlow = MutableStateFlow(0L)
//    private val timeTextFlow = MutableStateFlow("00:00:00")
//    private val hourFlow = MutableStateFlow(0)
//    private val minuteFlow = MutableStateFlow(0)
//    private val secondFlow = MutableStateFlow(0)
//    private val progressFlow = MutableStateFlow(0f)
//    private val isPlayingFlow = MutableStateFlow(false)
//    private val isDoneFlow = MutableStateFlow(true)
//
//    val timerState = combineTuple(
//        timeInMillisFlow,
//        timeTextFlow,
//        hourFlow,
//        minuteFlow,
//        secondFlow,
//        progressFlow,
//        isPlayingFlow,
//        isDoneFlow,
//    ).map { (timeInMillis, time, hour, minute, second, progress, isPlaying, isDone) ->
//        TimerState(
//            timeInMillis = timeInMillis,
//            hour = hour,
//            minute = minute,
//            second = second,
//            timeText = time,
//            progress = progress,
//            isPlaying = isPlaying,
//            isDone = isDone,
//        )
//    }
//
//    private var countDownTimerHelper: CountDownTimerHelper? = null
//
//    fun setTHour(hour: Int) {
//        hourFlow.value = hour
//    }
//
//    fun setMinute(minute: Int) {
//        minuteFlow.value = minute
//    }
//
//    fun setSecond(second: Int) {
//        secondFlow.value = second
//    }
//
//    fun setCountDownTimer() {
//        timeInMillisFlow.value =
//            (hourFlow.value.hours + minuteFlow.value.minutes + secondFlow.value.seconds).inWholeMilliseconds
//        countDownTimerHelper = object : CountDownTimerHelper(timeInMillisFlow.value, 1000) {
//            override fun onTimerTick(millisUntilFinished: Long) {
//                val progressValue = millisUntilFinished.toFloat() / timeInMillisFlow.value
//                handleTimerValues(true, millisUntilFinished.formatTime(), progressValue)
//            }
//            override fun onTimerFinish() {
//                workRequestManager.enqueueWorker<TimerCompletedWorker>(TIMER_COMPLETED_TAG)
//                reset()
//            }
//        }
//    }
//
//    fun pause() {
//        countDownTimerHelper?.pause()
//        isPlayingFlow.value = false
//    }
//
//    fun reset() {
//        countDownTimerHelper?.restart()
//        handleTimerValues(false, timeInMillisFlow.value.formatTime(), 0f)
//        isDoneFlow.value = true
//        workRequestManager.cancelWorker(TIMER_RUNNING_TAG)
//    }
//
//    fun start() {
//        countDownTimerHelper?.start()
//        isPlayingFlow.value = true
//        if (isDoneFlow.value) {
//            progressFlow.value = 1f
//            workRequestManager.enqueueWorker<TimerRunningWorker>(TIMER_RUNNING_TAG)
//            isDoneFlow.value = false
//        }
//    }
//
//    private fun handleTimerValues(
//        isPlaying: Boolean,
//        text: String,
//        progress: Float,
//    ) {
//        isPlayingFlow.value = isPlaying
//        timeTextFlow.value = text
//        progressFlow.value = progress
//    }
//
//    fun Long.formatTime(): String = String.format(
//        TIME_FORMAT,
//        TimeUnit.MILLISECONDS.toHours(this),
//        TimeUnit.MILLISECONDS.toMinutes(this) % 60,
//        TimeUnit.MILLISECONDS.toSeconds(this) % 60,
//    )
//}

import com.mhss.app.mybrain.domain.model.TimerState
import com.mhss.app.mybrain.util.timer.GlobalProperties.TIME_FORMAT
import com.mhss.app.mybrain.data.workManager.worker.TIMER_COMPLETED_TAG
import com.mhss.app.mybrain.data.workManager.worker.TIMER_RUNNING_TAG
import com.mhss.app.mybrain.data.workManager.worker.TimerCompletedWorker
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
import kotlin.math.absoluteValue
import kotlin.random.Random

@Singleton
class TimerManager @Inject constructor(
    private val workRequestManager: WorkRequestManager,
) {

    // Definir rangos para tiempo aleatorio
    private val minHours = 0
    private val maxHours = 24

    private val minMinutes = 0
    private val maxMinutes = 59

    private val minSeconds = 0
    private val maxSeconds = 59

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
    private fun generateRandomTime(): Long {
        val randomHours = (minHours..maxHours).random()
        val randomMinutes = (minMinutes..maxMinutes).random()
        val randomSeconds = (minSeconds..maxSeconds).random()
        return (randomHours.hours + randomMinutes.minutes + randomSeconds.seconds).inWholeMilliseconds
    }

    fun setCountDownTimer() {
        timeInMillisFlow.value =
           (hourFlow.value.hours + minuteFlow.value.minutes + secondFlow.value.seconds).inWholeMilliseconds

        // Inicializa el tiempo con un valor aleatorio
       // timeInMillisFlow.value = generateRandomTime()

        countDownTimerHelper = object : CountDownTimerHelper(timeInMillisFlow.value, 1000) {
//            override fun onTimerTick(millisUntilFinished: Long) {
//                // Genera un nuevo tiempo aleatorio cada tick
//                val newTime = generateRandomTime()
//                timeInMillisFlow.value = newTime
//
//                val progressValue = newTime.toFloat() / timeInMillisFlow.value
//                handleTimerValues(true, newTime.formatTime(), progressValue)
//            }
            private val coroutineScope = CoroutineScope(Dispatchers.Default)

            override fun onTimerTick(millisUntilFinished: Long) {
                coroutineScope.launch {
                    // Introduce a delay to keep the current value for a while
                    var progressValue = millisUntilFinished.toFloat() / timeInMillisFlow.value
                    handleTimerValues(true, millisUntilFinished.formatTime(), progressValue)

                    // Generate small, subtle random changes
                    val randomAdjustment = Random.nextLong(-10000, 50000).absoluteValue // Adjust by up to 10 seconds
                    val adjustedTime = (millisUntilFinished + randomAdjustment).coerceAtLeast(0) // Ensure it's non-negative


                    progressValue = adjustedTime.toFloat() / timeInMillisFlow.value
                    handleTimerValues(true, adjustedTime.formatTime(), progressValue)

                    delay(3000L) // 3 seconds; adjust this value to control how long it stays before changing
                }
            }

            override fun onTimerFinish() {
                // En lugar de finalizar, reinicia el temporizador con un nuevo tiempo
                setCountDownTimer()
                start()
            }
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