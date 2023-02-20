package org.example.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.Model.DayContainer;
import org.example.Model.Movie;
import org.example.Model.SalesDataPoint;
import org.example.Model.YearOnYearComparisonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class DataRestConfig implements RepositoryRestConfigurer {

    @Value("${allowed.origins}")
    private String[] allowedOrgins;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public DataRestConfig(){

    }
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {

        HttpMethod[] invalidActions = {HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PATCH};
        disableHttpMethods(Movie.class, config, invalidActions);
        disableHttpMethods(DayContainer.class, config, invalidActions);
        disableHttpMethods(SalesDataPoint.class, config, invalidActions);
        cors.addMapping(config.getBasePath() + "/**").allowedOrigins(allowedOrgins);

    }

    private static void disableHttpMethods(Class classType, RepositoryRestConfiguration config, HttpMethod[] invalidActions) {
        config.getExposureConfiguration().forDomainType(classType)

                .withItemExposure(((metdata, httpMethods) -> httpMethods.disable(invalidActions)))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(invalidActions));
    }

}
