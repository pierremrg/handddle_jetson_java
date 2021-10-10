package com.handddle.farm.farm_master.data_persisters;

public class HumidityDataPersister extends DataPersister {

    public HumidityDataPersister(String key, Object value) {
        super(key, value);
    }

    @Override
    public boolean shouldBePersisted() {
        return false;
    }

}
