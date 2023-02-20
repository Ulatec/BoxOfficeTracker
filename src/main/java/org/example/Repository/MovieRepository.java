package org.example.Repository;


import org.example.Model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.util.Optional;

@CrossOrigin("*")
@RepositoryRestResource
public interface MovieRepository extends PagingAndSortingRepository<Movie, Long> {
    Optional<Movie> findByTitle(String title);

    Page<Movie> findAllByTotalEstimatedRevenueGreaterThanOrderByTotalEstimatedRevenueDesc(int totalEstimatedRevenue,Pageable pageable);

    void save(Movie existingMovie);
}
