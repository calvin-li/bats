package app.calvin.bats;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import java.sql.Time;

public class Notification extends BroadcastReceiver {

    public static final String BATS_STOP = "app.calvin.bats.Notification.BATS_STOP";
    public static final int notificationID = 23;

    protected static AlarmManager manager;
    protected static PendingIntent alarmPendingIntent;
    protected static final int updateInterval = 1000*60;

    private static NotificationCompat.Builder batteryInfoBuilder;
    private static Intent batteryStatus;
    private static Boolean charging = false;
    private static int level, voltage;
    private static double temperature, lastChange;
    private static String plugged, status;
    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        //check if we should cancel notification instead of updating
        if(intent.hasExtra("Action")) {
            manager.cancel(alarmPendingIntent);
            mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(notificationID);
            return;
        }

        batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        level = getBatteryInfo(BatteryManager.EXTRA_HEALTH, context);
        plugged = plugToMessage(getBatteryInfo(BatteryManager.EXTRA_PLUGGED, context));
        status = statusToMessage(getBatteryInfo(BatteryManager.EXTRA_STATUS, context));
        temperature = getBatteryInfo(BatteryManager.EXTRA_TEMPERATURE, context) / 10.0;
        voltage = getBatteryInfo(BatteryManager.EXTRA_VOLTAGE, context);

        long timeInMilli = SystemClock.elapsedRealtime() - (long)lastChange;
        int days = (int)(timeInMilli / (1000 * 60 * 60 * 24) + 0.5),
                hours = (int)(timeInMilli / (1000 * 60 * 60)%24 + 0.5),
                minutes = (int)(timeInMilli / (1000 * 60)%60 + 0.5);
        String time = "Time formatting error";
        if(days > 0) {
            String formatString = "%d:%02d:%02d";
            time = String.format(formatString, days, hours, minutes);
        }
        else if(days == 0){
            String formatString = "%d:%02d";
            time = String.format(formatString, hours, minutes);
        }

        String notificationTitle = status + plugged + " for " + time;
        String notificationText = temperature + "\u00b0C / " + voltage + "mV";

        if(batteryInfoBuilder == null){
            batteryInfoBuilder = new NotificationCompat.Builder(context)
                .setOngoing(true);

            lastChange = SystemClock.elapsedRealtime();

            Intent resultIntent = new Intent(BATS_STOP);
            resultIntent.putExtra("Action", "Stop");
            PendingIntent resultPendingIntent = PendingIntent.getBroadcast(context, 0, resultIntent, 0);
            batteryInfoBuilder.setContentIntent(resultPendingIntent);
        }
        batteryInfoBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, batteryInfoBuilder.build());
    }

    private String plugToMessage(int state) {
        String ret = " ";
        if(state == BatteryManager.BATTERY_PLUGGED_AC)
            return ret + "(AC)";
        else if(state == BatteryManager.BATTERY_PLUGGED_USB)
            return ret + "(USB)";
        else if(state == BatteryManager.BATTERY_PLUGGED_WIRELESS)
            return ret + "(Wireless)";
        else {
            return "";
        }
    }

    private String statusToMessage(int state) {
        if(state == BatteryManager.BATTERY_STATUS_CHARGING) {
            if(!charging) {
                changeChargeState();
            }
            return "Charging";
        }
        else if(state == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            if(charging) {
                changeChargeState();
            }
            return "Discharging";
        }
        else if(state == BatteryManager.BATTERY_STATUS_FULL) {
            if(!charging) {
                changeChargeState();
            }
            return "Full";
        }
        else if(state == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            if(charging) {
                changeChargeState();
            }
            return "Not charging";
        }
        else if(state == BatteryManager.BATTERY_STATUS_UNKNOWN) {
            return "Status unknown";
        }
        else
            return "-1";
    }

    private void changeChargeState() {
        charging = !charging;
        lastChange = SystemClock.elapsedRealtime();
    }

    protected int getBatteryInfo(String extra, Context context){
        int defaultValue = -1;
        int ret = batteryStatus.getIntExtra(extra, defaultValue);
        String errorMessage = "Attribute " + extra + " could not be fetched.";
        if(ret == defaultValue) {
           MainActivity.makeToast(errorMessage, context.getApplicationContext());
        }
        return ret;
    }

}
