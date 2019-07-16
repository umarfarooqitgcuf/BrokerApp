package net.itempire.brokerapp;


public class DriverHistoryCustom {
    private int id;
    private String rideDate, rideAmmount, rideStarted, rideEnded, ridername;

    public DriverHistoryCustom() {
        this.id = id;
        this.rideDate = rideDate;
        this.rideAmmount = rideAmmount;
        this.rideStarted = rideStarted;
        this.rideEnded = rideEnded;
        this.ridername = ridername;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRideDate() {
        return rideDate;
    }

    public void setRideDate(String rideDate) {
        this.rideDate = rideDate;
    }

    public String getRideAmmount() {
        return rideAmmount;
    }

    public void setRideAmmount(String rideAmmount) {
        this.rideAmmount = rideAmmount;
    }

    public String getRideStarted() {
        return rideStarted;
    }

    public void setRideStarted(String rideStarted) {
        this.rideStarted = rideStarted;
    }

    public String getRideEnded() {
        return rideEnded;
    }

    public void setRideEnded(String rideEnded) {
        this.rideEnded = rideEnded;
    }

    public String getRidername() {
        return ridername;
    }

    public void setRidername(String ridername) {
        this.ridername = ridername;
    }
}
