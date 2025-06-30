// NotificationScheduler.java
package com.example.taskmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationScheduler {

    public static void schedule(Context context, long id,
                                String title, String desc, String due) {
        long triggerAt = parseMillis(due);
        if (triggerAt < System.currentTimeMillis()) return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("desc", desc);

        PendingIntent pi = PendingIntent.getBroadcast(
                context, (int) id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAt, pi
            );
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        }
    }

    private static long parseMillis(String due) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm", Locale.getDefault()
            );
            Date d = fmt.parse(due);
            return d != null ? d.getTime() : 0L;
        } catch (ParseException e) {
            return 0L;
        }
    }
}
