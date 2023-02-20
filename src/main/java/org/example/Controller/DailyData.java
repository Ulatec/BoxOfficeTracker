package org.example.Controller;


import org.example.Model.DayContainer;
import org.example.Model.YearOnYearComparisonObject;
import org.example.Repository.DayContainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@CrossOrigin
@RestController
public class DailyData {

    @Autowired
    private DayContainerRepository dayContainerRepository;

    @GetMapping("getCurrentYearOnYearDifference/{year}")
    public List<YearOnYearComparisonObject> getCurrentYearOnYearDifference(@PathVariable int year){
        LocalDate today = LocalDate.now();
        LocalDate startOfYear = LocalDate.of(today.getYear(), 1,1);
        LocalDate todayInPastYear = LocalDate.of(year, today.getMonth(),today.getDayOfMonth());
        LocalDate startOfPastYear = LocalDate.of(year, 1,1);

        List<YearOnYearComparisonObject> outputList = new ArrayList<>();
        //current Year
        LocalDate trackingDate = startOfYear;
        long dollarTotal= 0;
        //loop through current year to calculate current year's data.
        while(trackingDate.isBefore(today) || trackingDate.isEqual(today)){
            Date date = Date.from(trackingDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            YearOnYearComparisonObject yearOnYearComparisonObject = new YearOnYearComparisonObject();
            yearOnYearComparisonObject.setDate(date);

            Optional<DayContainer> optionalDayContainer = dayContainerRepository.findByDate(date);
            if(optionalDayContainer.isPresent()){
                dollarTotal += optionalDayContainer.get().getDollars();
            }
            yearOnYearComparisonObject.setCurrentYearDollars(dollarTotal);
            outputList.add(yearOnYearComparisonObject);
            trackingDate = trackingDate.plusDays(1);
        }

        trackingDate = startOfPastYear;
        dollarTotal = 0;
        //loop through prior year data points to calculate prior year's data.
        while(trackingDate.isBefore(todayInPastYear) || trackingDate.isEqual(todayInPastYear)){
            Date lookupDate = Date.from(LocalDate.of(year,trackingDate.getMonthValue(),trackingDate.getDayOfMonth()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Date date = Date.from(LocalDate.of(today.getYear(),trackingDate.getMonthValue(),trackingDate.getDayOfMonth()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            YearOnYearComparisonObject trackingObject;
            for(YearOnYearComparisonObject yearOnYearComparisonObject : outputList){
                if(yearOnYearComparisonObject.getDate().equals(date)){
                    trackingObject = yearOnYearComparisonObject;
                    Optional<DayContainer> optionalDayContainer = dayContainerRepository.findByDate(lookupDate);
                    if(optionalDayContainer.isPresent()){
                        dollarTotal += optionalDayContainer.get().getDollars();
                    }
                    trackingObject.setPastYearDollars(dollarTotal);
                    if(dollarTotal!= 0) {
                        //calculate return percentage
                        trackingObject.setPercentageDifference((double)(trackingObject.getCurrentYearDollars() - dollarTotal)/(double)dollarTotal);
                    }else{
                        trackingObject.setPercentageDifference(Double.POSITIVE_INFINITY);
                    }
                    break;
                }
            }
            trackingDate = trackingDate.plusDays(1);
        }
        //Reverse data to be presented in chronological order
        Collections.reverse(outputList);
        return outputList;
    }
}
