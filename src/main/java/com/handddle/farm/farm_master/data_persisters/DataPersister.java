package com.handddle.farm.farm_master.data_persisters;

public abstract class DataPersister {

    protected final String key;
    protected final int value;

    public DataPersister(String key, Object value){
        this.key = key;
        this.value = (int) (long) value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public boolean shouldBePersisted(){
        return true;
    }

}