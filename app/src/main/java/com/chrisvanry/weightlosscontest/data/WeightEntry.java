package com.chrisvanry.weightlosscontest.data;

// WeightEntry Object class

public class WeightEntry {

    public String weekNum, weightData;

    public WeightEntry() {
    }

    public WeightEntry(String weekNum, String weightData) {
        this.weekNum = weekNum;
        this.weightData = weightData;
    }

    public String getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(String weekNum) {
        this.weekNum = weekNum;
    }

    public String getWeightData() {
        return weightData;
    }

    public void setWeightData(String weightData) {
        this.weightData = weightData;
    }

    @Override
    public String toString() {
        return "WeightEntry{" +
                "weekNum='" + weekNum + '\'' +
                ", weightData='" + weightData + '\'' +
                '}';
    }
}
