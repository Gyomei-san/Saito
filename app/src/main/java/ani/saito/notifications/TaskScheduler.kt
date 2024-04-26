package ani.saito.notifications

import android.content.Context
import ani.saito.notifications.anilist.AnilistNotificationWorker
import ani.saito.notifications.comment.CommentNotificationWorker
import ani.saito.notifications.subscription.SubscriptionNotificationWorker
import ani.saito.settings.saving.PrefManager
import ani.saito.settings.saving.PrefName

interface TaskScheduler {
    fun scheduleRepeatingTask(taskType: TaskType, interval: Long)
    fun cancelTask(taskType: TaskType)

    fun cancelAllTasks() {
        for (taskType in TaskType.entries) {
            cancelTask(taskType)
        }
    }

    fun scheduleAllTasks(context: Context) {
        for (taskType in TaskType.entries) {
            val interval = when (taskType) {
                TaskType.COMMENT_NOTIFICATION -> CommentNotificationWorker.checkIntervals[PrefManager.getVal(
                    PrefName.CommentNotificationInterval)]
                TaskType.ANILIST_NOTIFICATION -> AnilistNotificationWorker.checkIntervals[PrefManager.getVal(
                    PrefName.AnilistNotificationInterval)]
                TaskType.SUBSCRIPTION_NOTIFICATION -> SubscriptionNotificationWorker.checkIntervals[PrefManager.getVal(
                    PrefName.SubscriptionNotificationInterval)]
            }
            scheduleRepeatingTask(taskType, interval)
        }
    }

    companion object {
        fun create(context: Context, useAlarmManager: Boolean): TaskScheduler {
            return if (useAlarmManager) {
                AlarmManagerScheduler(context)
            } else {
                WorkManagerScheduler(context)
            }
        }
    }
    enum class TaskType {
        COMMENT_NOTIFICATION,
        ANILIST_NOTIFICATION,
        SUBSCRIPTION_NOTIFICATION
    }
}

interface Task {
    suspend fun execute(context: Context): Boolean
}
