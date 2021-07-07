package com.chrisvanry.weightlosscontest.data;

// User object class

public class User {

    public String firstName, lastName, email, competitionId;

    public User() {

    }

    // constructor
    public User(String firstName, String lastName, String email, String competitionId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.competitionId = competitionId;
    }

    // getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(String competitionId) {
        this.competitionId = competitionId;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", competitionId='" + competitionId + '\'' +
                '}';
    }
}
