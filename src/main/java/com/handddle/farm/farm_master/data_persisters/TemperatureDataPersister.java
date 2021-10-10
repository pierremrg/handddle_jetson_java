package com.handddle.farm.farm_master.data_persisters;

public class TemperatureDataPersister extends DataPersister {

    public TemperatureDataPersister(String key, Object value) {
        super(key, value);
    }

    @Override
    public Object getValue() {
        return (float) value / 100;
    }

}
