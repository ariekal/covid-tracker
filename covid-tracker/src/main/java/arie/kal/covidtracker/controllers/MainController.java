package arie.kal.covidtracker.controllers;

import arie.kal.covidtracker.models.LocationData;
import arie.kal.covidtracker.services.CovidDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    CovidDataService covidDataService;

    @GetMapping("/")
    public String main(Model model) {
        List<LocationData> allData = covidDataService.getAllData();
        int totalReportedCases = allData.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allData.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        model.addAttribute("locData", allData);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
        return "main";
    }
}
