package in.sunilpaulmathew.batteryplus.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.batteryplus.serializables.DataEntry;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 07, 2026
 */
public class Tracker {

    public static void appendHistoryPoint(String prefPrefix, long timestamp, float value, Context context) {
        String historyKey = prefPrefix + "_history_data";
        String existing = Utils.getString(historyKey, "", context);

        if (!existing.isEmpty()) {
            String[] points = existing.split(",");
            if (points.length > 0) {
                String lastPointStr = points[points.length - 1];
                String[] sub = lastPointStr.split(":");
                if (sub.length == 2) {
                    try {
                        float lastValue = Float.parseFloat(sub[1]);
                        if (lastValue == value) {
                            return;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        StringBuilder sb = new StringBuilder(existing);
        if (sb.length() > 0) sb.append(",");
        sb.append(timestamp).append(":").append(value);

        String[] points = sb.toString().split(",");
        if (points.length > 500) {
            sb = new StringBuilder();
            for (int i = points.length - 500; i < points.length; i++) {
                if (sb.length() > 0) sb.append(",");
                sb.append(points[i]);
            }
        }

        Utils.saveString(historyKey, sb.toString(), context);
    }

    public static List<DataEntry> getHistory(String prefPrefix, Context context) {
        List<DataEntry> list = new ArrayList<>();
        String historyKey = prefPrefix + "_history_data";
        String raw = Utils.getString(historyKey, "", context);
        if (raw.isEmpty()) return list;

        String[] parts = raw.split(",");
        for (String p : parts) {
            String[] sub = p.split(":");
            if (sub.length == 2) {
                try {
                    long t = Long.parseLong(sub[0]);
                    float v = Float.parseFloat(sub[1]);
                    list.add(new DataEntry(t, v));
                } catch (NumberFormatException ignored) {}
            }
        }
        return list;
    }

    public static void clearHistory(String prefPrefix, Context context) {
        Utils.saveString(prefPrefix + "_history_data", "", context);
        Utils.saveLong(prefPrefix + "_last_level", -1, context);
        Utils.saveLong(prefPrefix + "_last_timestamp", -1, context);
    }

}