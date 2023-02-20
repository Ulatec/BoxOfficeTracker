package org.example.Model;


import jakarta.persistence.*;


import java.util.Date;

@Entity
public class SalesDataPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    public Movie movie;

    public Date date;
    public int theaters;
    public long estimatedRevenue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public int getTheaters() {
        return theaters;
    }

    public void setTheaters(int theaters) {
        this.theaters = theaters;
    }

    public long getEstimatedRevenue() {
        return estimatedRevenue;
    }

    public void setEstimatedRevenue(long estimatedRevenue) {
        this.estimatedRevenue = estimatedRevenue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
