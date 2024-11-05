package com.mhss.app.mybrain.data.workManager.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.mhss.app.mybrain.data.manager.TimerManager
import com.mhss.app.mybrain.util.timer.TIMER_RUNNING_NOTIFICATION_ID
import com.mhss.app.mybrain.util.timer.TimerNotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest

@HiltWorker
class TimerRunningWorker @AssistedInject constructor(
    @Assisted private val timerManager: TimerManager,
    @Assisted private val timerNotificationHelper: TimerNotificationHelper,
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
            val foregroundInfo = ForegroundInfo(
                TIMER_RUNNING_NOTIFICATION_ID,
                timerNotificationHelper.getTimerBaseNotification().build(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK else 0
            )

            setForeground(foregroundInfo)

            timerManager.timerState.collectLatest {
                if (!it.isDone) {
                    timerNotificationHelper.updateTimerServiceNotification(
                        isPlaying = it.isPlaying,
                        timeText = it.timeText,
                    )
                }
            }

            Result.success()
        } catch (e: CancellationException) {
            timerNotificationHelper.removeTimerRunningNotification()
            Result.failure()
        }
    }
}

const val TIMER_RUNNING_TAG = "timerRunningTag"