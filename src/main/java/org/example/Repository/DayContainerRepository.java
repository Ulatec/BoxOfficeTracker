package org.example.Repository;


import org.example.Model.DayContainer;
import org.example.Model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Date;
import java.util.Optional;

public interface DayContainerRepository extends JpaRepository<DayContainer, Long> {

    Optional<DayContainer> findByDate(Date date);
}
