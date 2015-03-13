package app.calvin.bats;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class Notification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity.makeToast("Hi!", context);

        MainActivity.level = getBatteryInfo(BatteryManager.EXTRA_HEALTH);
        MainActivity.plugged = plugToMessage(getBatteryInfo(BatteryManager.EXTRA_PLUGGED));
        MainActivity.status = statusToMessage(getBatteryInfo(BatteryManager.EXTRA_STATUS));
        MainActivity.temperature = getBatteryInfo(BatteryManager.EXTRA_TEMPERATURE) / 10;
        MainActivity.voltage = getBatteryInfo(BatteryManager.EXTRA_VOLTAGE);

        //Build notification
        String notificationTitle = MainActivity.status + MainActivity.plugged;
        String notificationText = MainActivity.temperature + "\u00b0C / " + MainActivity.voltage + "mV";

        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(context)
                        .setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText);
        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        nBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MainActivity.notificationID, nBuilder.build());

    }

    private String plugToMessage(int state) {
        String ret = " - ";
        if(state == BatteryManager.BATTERY_PLUGGED_AC)
            return ret + "AC";
        else if(state == BatteryManager.BATTERY_PLUGGED_USB)
            return ret + "USB";
        else if(state == BatteryManager.BATTERY_PLUGGED_WIRELESS)
            return ret + "Wireless";
        else
            return "";
    }

    private String statusToMessage(int state) {
        if(state == BatteryManager.BATTERY_STATUS_CHARGING)
            return "Charging";
        else if(state == BatteryManager.BATTERY_STATUS_DISCHARGING)
            return "Discharging";
        else if(state == BatteryManager.BATTERY_STATUS_FULL)
            return "Full";
        else if(state == BatteryManager.BATTERY_STATUS_NOT_CHARGING)
            return "Not charging";
        else if(state == BatteryManager.BATTERY_STATUS_UNKNOWN)
            return "Status unknown";
        else
            return "-1";
    }

    protected static int getBatteryInfo(String extra){
        int defaultValue = -1;
        int ret = MainActivity.batteryStatus.getIntExtra(extra, defaultValue);
        String errorMessage = "Attribute " + extra + " could not be fetched.";
        if(ret == defaultValue) {
            //makeToast(errorMessage, this);
        }
        return ret;
    }

}
