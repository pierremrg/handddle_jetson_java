package com.handddle.farm.farm_master.data_persisters;

import java.sql.Timestamp;

public class DoorClosedDataPersister extends DataPersister {

    private static int lastValue = -1;
    private static int lastInsertDate = (int) Math.floor(new Timestamp(System.currentTimeMillis()).getTime() / 1000.0);

    public DoorClosedDataPersister(String key, Object value) {
        super(key, value);
    }

    @Override
    public boolean shouldBePersisted() {
        int now = (int) Math.floor(new Timestamp(System.currentTimeMillis()).getTime() / 1000.0);

        if(value != lastValue || lastInsertDate < now - 10){
            lastValue = value;
            lastInsertDate = now;
            return true;
        }

        return false;
    }

}
