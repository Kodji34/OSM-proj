package com.example.school.controller;

import com.example.school.entity.AppSettings;
import com.example.school.repository.AppSettingsRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttributes {

    private final AppSettingsRepository appSettingsRepository;

    public GlobalModelAttributes(AppSettingsRepository appSettingsRepository) {
        this.appSettingsRepository = appSettingsRepository;
    }

    @ModelAttribute
    public void addTheme(Model model) {
        String primary = appSettingsRepository.findById(1L)
                .map(AppSettings::getThemePrimary)
                .orElse("#1E88E5");
        if (primary == null || primary.isBlank()) {
            primary = "#1E88E5";
        }
        model.addAttribute("appThemePrimary", primary);
    }
}
