package org.example.Repository;


import org.example.Model.Movie;
import org.example.Model.SalesDataPoint;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SalesDataPointRepository extends JpaRepository<SalesDataPoint, Long> {
}
