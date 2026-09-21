package in.sunilpaulmathew.batteryplus.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.sunilpaulmathew.batteryplus.services.BatteryMonitorService;
import in.sunilpaulmathew.batteryplus.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 07, 2026
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Utils.getBoolean("startOnBoot", false, context)) return;

        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || "android.intent.action.QUICKBOOT_POWERON".equals(action)
                || "com.htc.intent.action.QUICKBOOT_POWERON".equals(action)) {

            boolean isEnabled = Utils.getBoolean("showNotification", false, context);

            if (isEnabled && Utils.hasNotificationGranted(context)) {
                Intent serviceIntent = new Intent(context, BatteryMonitorService.class);
                context.startForegroundService(serviceIntent);
            }
        }
    }

}