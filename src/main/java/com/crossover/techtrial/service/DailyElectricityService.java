package com.crossover.techtrial.service;

import com.crossover.techtrial.dto.DailyElectricity;

import java.util.List;

/**
 * Created by Alex on 24.11.2018.
 */
public interface DailyElectricityService {
    List<DailyElectricity> getDailyElectricityForPanel(Long panelId);
}