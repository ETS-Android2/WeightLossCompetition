package com.chrisvanry.weightlosscontest.data;

// Competition object class

public class Competition {

    public String ownerId, name, startDate, length;

    public Competition() {

    }

    // constructor
    public Competition(String ownerId, String name, String startDate, String length) {
        this.ownerId = ownerId;
        this.name = name;
        this.startDate = startDate;
        this.length = length;
    }

    // getters and setters
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return  "Competition{" +
                "ownerId='" + ownerId + '\'' +
                "name='" + name + '\'' +
                ", startDate='" + startDate + '\'' +
                ", length='" + length + '\'' +
                '}';
    }
}