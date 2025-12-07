package sensorservice.web.controller;

import sensorservice.domain.entity.Station;
import sensorservice.service.SensorMeasurementService;
import sensorservice.service.StationService;
import sensorservice.web.dto.MeasurementCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MeasurementUiController {

    private final StationService stationService;
    private final SensorMeasurementService measurementService;

    @GetMapping("/ui/measurements/new")
    public String showForm(Model model) {
        List<Station> stations = stationService.findAll();
        model.addAttribute("stations", stations);
        model.addAttribute("measurementForm", new MeasurementCreateRequest());
        return "measurement-form"; // nom du template Thymeleaf
    }

    @PostMapping("/ui/measurements")
    public String submitForm(
            @Valid @ModelAttribute("measurementForm") MeasurementCreateRequest form,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("stations", stationService.findAll());
            return "measurement-form";
        }

        measurementService.createManualMeasurement(
                form.getStationId(),
                form.getPh(),
                form.getTemperature(),
                form.getTurbidity(),
                form.getDissolvedOxygen(),
                form.getConductivity()
        );

        // après insertion, on recharge une page de confirmation ou le même formulaire
        return "redirect:/ui/measurements/new?success";
    }
}