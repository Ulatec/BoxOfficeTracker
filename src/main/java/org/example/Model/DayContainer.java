package org.example.Model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
@Entity

public class DayContainer {

    public Date date;

    public long dollars;

    @OneToMany(fetch = FetchType.EAGER)
    List<SalesDataPoint> salesDataPoints;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDollars() {
        return dollars;
    }

    public void setDollars(long dollars) {
        this.dollars = dollars;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SalesDataPoint> getSalesDataPoints() {
        return salesDataPoints;
    }

    public void setSalesDataPoints(List<SalesDataPoint> salesDataPoints) {
        this.salesDataPoints = salesDataPoints;
    }
}
