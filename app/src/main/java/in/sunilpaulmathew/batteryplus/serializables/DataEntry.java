package in.sunilpaulmathew.batteryplus.serializables;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 07, 2026
 */
public class DataEntry implements Serializable {

    private final float value;
    private final long timestamp;

    public DataEntry(long timestamp, float value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public float getValue() {
        return this.value;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

}