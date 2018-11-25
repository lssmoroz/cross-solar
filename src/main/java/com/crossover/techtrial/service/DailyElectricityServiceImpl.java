package com.crossover.techtrial.service;

import com.crossover.techtrial.dto.DailyElectricity;
import com.crossover.techtrial.model.HourlyElectricity;
import com.crossover.techtrial.repository.HourlyElectricityRepository;
import com.crossover.techtrial.repository.PanelRepository;
import org.hibernate.boot.model.source.spi.Sortable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 24.11.2018.
 */
@Service
public class DailyElectricityServiceImpl implements DailyElectricityService {
    @Autowired
    HourlyElectricityRepository hourlyElectricityRepository;

    public List<DailyElectricity> getDailyElectricityForPanel(Long panelId) {
        List<DailyElectricity> dailyElectricityForPanel = new ArrayList<>();

        List<HourlyElectricity> hourlyElectricityList =
                hourlyElectricityRepository.findAllByPanelIdAndReadingAtBeforeOrderByReadingAtDesc(panelId, LocalDate.now().atStartOfDay());

        DailyElectricity dailyElectricity = null;
        LocalDate dateNow = null;
        int counter = 0;
        for (HourlyElectricity hourlyElectricity : hourlyElectricityList) {
            if (null == dateNow || !dateNow.isEqual(hourlyElectricity.getReadingAt().toLocalDate())) {
                dateNow = hourlyElectricity.getReadingAt().toLocalDate();
                if (null != dailyElectricity) {
                    dailyElectricity.setAverage(((double)dailyElectricity.getSum())/counter);
                }
                dailyElectricity = new DailyElectricity();
                dailyElectricity.setDate(dateNow);
                dailyElectricityForPanel.add(dailyElectricity);
                counter = 0;
            }
            Long generatedNow = hourlyElectricity.getGeneratedElectricity();

            dailyElectricity.setSum(dailyElectricity.getSum()+generatedNow);

            if (dailyElectricity.getMax() < generatedNow) {
                dailyElectricity.setMax(generatedNow);
            }

            if (dailyElectricity.getMin() > generatedNow) {
                dailyElectricity.setMin(generatedNow);
            }

            ++counter;
        }
        if (null != dailyElectricity) {
            dailyElectricity.setAverage(((double)dailyElectricity.getSum())/counter);
        }

        return dailyElectricityForPanel;
    }

}