package com.chrisvanry.weightlosscontest.data;

// Entry object class

public class Entry {

    public String userId, competitionId, date, weight;

    public Entry() {

    }

    public Entry(String userId, String competitionId, String date, String weight) {
        this.userId = userId;
        this.competitionId = competitionId;
        this.date = date;
        this.weight = weight;
    }

}