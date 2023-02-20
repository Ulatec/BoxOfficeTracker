package org.example.Worker;

import org.example.Model.DayContainer;
import org.example.Model.Movie;
import org.example.Model.SalesDataPoint;
import org.example.Repository.DayContainerRepository;
import org.example.Repository.MovieRepository;
import org.example.Repository.SalesDataPointRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class MainWorker {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private DayContainerRepository dayContainerRepository;
    @Autowired
    private SalesDataPointRepository salesDataPointRepository;

    @Scheduled(fixedRate = 6000000)
    public void run() throws Exception {

        LocalDate localDate = LocalDate.now();
        List<Integer> years = new ArrayList<>();
        //hard-coded 5 years. Can be any number.
        for(int i = 0; i < 5; i++) {
            years.add(localDate.getYear()-i);

        }
        for(int year : years) {
            try {
                Connection.Response mainResponse = Jsoup.connect("https://www.boxofficemojo.com/daily/" + year + "/?view=year")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .execute();

                Document doc = mainResponse.parse();
                Elements elements = doc.getElementsByTag("tr");
                for (Element element : elements) {
                    if (element.getElementsByTag("td").size() > 0) {
                        Element linkElement = element.getElementsByTag("td").get(0);
                        String dayLink = "https://www.boxofficemojo.com" + linkElement.getElementsByTag("a").attr("href");
                        String safeString = dayLink;
                        safeString = safeString.replace("https://www.boxofficemojo.com/date/", "");
                        Thread.sleep(200);
                        Date date = simpleDateFormat.parse(safeString.substring(0, safeString.indexOf("/")));
                        Connection.Response dailyDataResponse = Jsoup.connect(dayLink)
                                .method(Connection.Method.GET)
                                .ignoreContentType(true)
                                .execute();

                        Elements movieRows = dailyDataResponse.parse().getElementsByTag("tr");
                        long dailymoney = 0;
                        for (Element movieRow : movieRows) {
                            if (movieRow.getElementsByTag("td").size() > 0) {
                                String movieName = movieRow.getElementsByTag("td").get(2).text();
                                int theaterCount;
                                try {
                                    theaterCount = Integer.parseInt(movieRow.getElementsByTag("td").get(6).text().replace(",", "").replace("-", ""));
                                }catch (NumberFormatException numberFormatException){
                                    //If an element comes back with an unreadable field, such as '-'
                                    theaterCount = 0;
                                }
                                Element moneyElement = movieRow.getElementsByTag("td").get(3);
                                long dollars = Long.parseLong(moneyElement.text().replace("$", "").replace(",", ""));
                                dailymoney = dailymoney + dollars;
                                Optional<Movie> existingMovieOptional = findIfMovieAlreadyInDatabase(movieName);
                                if(existingMovieOptional.isPresent()){
                                    Movie existingMovie = existingMovieOptional.get();
                                    SalesDataPoint existingDataPoint = null;
                                    for(SalesDataPoint salesDataPoint1 : existingMovie.getDataPointList()){
                                        if(salesDataPoint1.getDate() == date){
                                            existingDataPoint = salesDataPoint1;
                                            break;
                                        }
                                    }
                                    if(existingDataPoint != null){
                                        if(existingDataPoint.getEstimatedRevenue() != dollars){
                                            //data point is not the same, update it.
                                            existingDataPoint.setEstimatedRevenue(dollars);
                                            existingDataPoint.setTheaters(theaterCount);
                                            salesDataPointRepository.save(existingDataPoint);
                                        }
                                    }else{
                                        SalesDataPoint salesDataPoint1 = new SalesDataPoint();
                                        salesDataPoint1.setEstimatedRevenue(dollars);
                                        salesDataPoint1.setTheaters(theaterCount);
                                        salesDataPoint1.setDate(date);
                                        DayContainer dayContainer = prepareDayContainer(findIfDayAlreadyInDatabase(date),salesDataPoint1,date);
                                        salesDataPointRepository.save(salesDataPoint1);
                                        List<SalesDataPoint> existingDataPoints = existingMovie.getDataPointList();
                                        existingDataPoints.add(salesDataPoint1);
                                        writeMovieAndSaleDataPoint(existingMovie, salesDataPoint1, dayContainer, existingDataPoints);
                                    }
                                }else {
                                    Movie movie = new Movie();
                                    //Get the imageUrl and ReleaseDate. Only needed if the movie isn't already in the database.
                                    getMovieImageAndReleaseDateUrl(movieRow.getElementsByTag("td").get(2).getElementsByTag("a").get(0).attr("href"),movie);
                                    movie.setTitle(movieName);
                                    SalesDataPoint salesDataPoint1 = new SalesDataPoint();
                                    salesDataPoint1.setEstimatedRevenue(dollars);
                                    salesDataPoint1.setTheaters(theaterCount);
                                    salesDataPoint1.setDate(date);
                                    List<SalesDataPoint> list = new ArrayList<>();
                                    list.add(salesDataPoint1);
                                    DayContainer dayContainer = prepareDayContainer(findIfDayAlreadyInDatabase(date),salesDataPoint1,date);

                                    salesDataPointRepository.save(salesDataPoint1);
                                    writeMovieAndSaleDataPoint(movie, salesDataPoint1, dayContainer, list);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private DayContainer prepareDayContainer(Optional<DayContainer> dayContainerOptional, SalesDataPoint salesDataPoint, Date date){
        DayContainer dayContainer;
        if(dayContainerOptional.isPresent()){
            dayContainer = dayContainerOptional.get();
            List<SalesDataPoint> list = dayContainer.getSalesDataPoints();
            list.add(salesDataPoint);
            dayContainer.setSalesDataPoints(list);
        }else{
            List<SalesDataPoint> list = new ArrayList<>();
            list.add(salesDataPoint);
            dayContainer = new DayContainer();
            dayContainer.setDate(date);
            dayContainer.setSalesDataPoints(list);
        }
        return dayContainer;
    }
    private void writeMovieAndSaleDataPoint(Movie movie, SalesDataPoint salesDataPoint1, DayContainer dayContainer1, List<SalesDataPoint> list) {
        movie.setDataPointList(list);
        long dollars2 = 0;
        for(SalesDataPoint salesDataPoint : movie.getDataPointList()){
            dollars2 =  dollars2 + salesDataPoint.getEstimatedRevenue();
        }
        movie.setTotalEstimatedRevenue(dollars2);
        movieRepository.save(movie);
        salesDataPoint1.setMovie(movie);
        salesDataPointRepository.save(salesDataPoint1);
        long dollars1 = 0;
        for(SalesDataPoint salesDataPoint : dayContainer1.getSalesDataPoints()){
            dollars1 =  dollars1 + salesDataPoint.getEstimatedRevenue();
        }
        dayContainer1.setDollars(dollars1);
        dayContainerRepository.save(dayContainer1);
    }

    public void getMovieImageAndReleaseDateUrl(String moviePageURL, Movie movie) {
        try {
            Connection.Response moviePage = Jsoup.connect("https://www.boxofficemojo.com/" + moviePageURL)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            Document document = moviePage.parse();
            Elements elements = document.getElementsByClass("mojo-posters");
            Element imageElement = elements.get(0).getElementsByTag("img").get(0);
            String imageUrl = imageElement.attr("src");
            movie.setImageUrl(imageUrl);

            //search all span tags for "Release Date"
            Elements elements2 = document.getElementsByTag("span");
            Element releaseDateSpan = null;
            for(Element span : elements2){
                if(span.text().contains("Release Date")){
                    releaseDateSpan = span;
                    break;
                }
            }
            //read the release date element if we found it.
            if(releaseDateSpan != null){
                //get parent element in order to dive into adjacent element.
                Element dateSpan = releaseDateSpan.parent().getElementsByTag("span").get(1);

                String dateText = dateSpan.text();
                if(dateText.contains("(")){
                    dateText = dateText.substring(0,dateSpan.text().indexOf("("));
                }
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                movie.setReleaseDate(sdf.parse(dateText));
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public Optional<Movie> findIfMovieAlreadyInDatabase(String movieTitle){
        return movieRepository.findByTitle(movieTitle);
    }
    public Optional<DayContainer> findIfDayAlreadyInDatabase(Date date){
        return dayContainerRepository.findByDate(date);
    }
}
