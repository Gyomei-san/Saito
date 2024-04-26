package ani.saito.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import ani.saito.notifications.anilist.AnilistNotificationReceiver
import ani.saito.notifications.comment.CommentNotificationReceiver
import ani.saito.notifications.TaskScheduler.TaskType
import ani.saito.notifications.subscription.SubscriptionNotificationReceiver
import ani.saito.settings.saving.PrefManager
import ani.saito.settings.saving.PrefName
import java.util.concurrent.TimeUnit

class AlarmManagerScheduler(private val context: Context) : TaskScheduler {
    override fun scheduleRepeatingTask(taskType: TaskType, interval: Long) {
        if (interval < TimeUnit.MINUTES.toMillis(15)) {
            cancelTask(taskType)
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = when (taskType) {
            TaskType.COMMENT_NOTIFICATION -> Intent(
                context,
                CommentNotificationReceiver::class.java
            )

            TaskType.ANILIST_NOTIFICATION -> Intent(
                context,
                AnilistNotificationReceiver::class.java
            )

            TaskType.SUBSCRIPTION_NOTIFICATION -> Intent(
                context,
                SubscriptionNotificationReceiver::class.java
            )
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskType.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(interval)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            PrefManager.setVal(PrefName.UseAlarmManager, false)
            TaskScheduler.create(context, true).cancelAllTasks()
            TaskScheduler.create(context, false).scheduleAllTasks(context)
        }
    }

    override fun cancelTask(taskType: TaskType) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = when (taskType) {
            TaskType.COMMENT_NOTIFICATION -> Intent(
                context,
                CommentNotificationReceiver::class.java
            )

            TaskType.ANILIST_NOTIFICATION -> Intent(
                context,
                AnilistNotificationReceiver::class.java
            )

            TaskType.SUBSCRIPTION_NOTIFICATION -> Intent(
                context,
                SubscriptionNotificationReceiver::class.java
            )
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskType.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}