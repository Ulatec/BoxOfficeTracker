package org.example.Model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;
    String imageUrl;

    Date releaseDate;
    long totalEstimatedRevenue;
    @OneToMany(fetch = FetchType.EAGER)
    List<SalesDataPoint> dataPointList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTotalEstimatedRevenue() {
        return totalEstimatedRevenue;
    }

    public void setTotalEstimatedRevenue(long totalEstimatedRevenue) {
        this.totalEstimatedRevenue = totalEstimatedRevenue;
    }

    public List<SalesDataPoint> getDataPointList() {
        return dataPointList;
    }

    public void setDataPointList(List<SalesDataPoint> dataPointList) {
        this.dataPointList = dataPointList;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
