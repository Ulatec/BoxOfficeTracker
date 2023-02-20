package org.example.Model;

import java.util.Date;

public class YearOnYearComparisonObject {

    public long currentYearDollars;

    public long pastYearDollars;

    public double percentageDifference;

    public Date date;

    public long getCurrentYearDollars() {
        return currentYearDollars;
    }

    public void setCurrentYearDollars(long currentYearDollars) {
        this.currentYearDollars = currentYearDollars;
    }

    public long getPastYearDollars() {
        return pastYearDollars;
    }

    public void setPastYearDollars(long pastYearDollars) {
        this.pastYearDollars = pastYearDollars;
    }

    public double getPercentageDifference() {
        return percentageDifference;
    }

    public void setPercentageDifference(double percentageDifference) {
        this.percentageDifference = percentageDifference;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
