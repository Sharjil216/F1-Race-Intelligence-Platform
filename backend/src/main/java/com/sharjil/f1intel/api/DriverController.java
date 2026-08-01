package com.sharjil.f1intel.api;

import com.sharjil.f1intel.domain.model.DriverInfo;
import com.sharjil.f1intel.ingestion.DriverIngestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class DriverController {

    private final DriverIngestionService driverService;

    public DriverController(DriverIngestionService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/drivers")
    public List<DriverInfo> getDrivers(@RequestParam int sessionKey) {
        return driverService.getDrivers(sessionKey);
    }
}
