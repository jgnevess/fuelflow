package br.com.joaonevesdev.fuelflow.api.controller;

import br.com.joaonevesdev.fuelflow.api.model.dto.FuelPriceResponse;
import br.com.joaonevesdev.fuelflow.api.model.dto.FuelStationResponse;
import br.com.joaonevesdev.fuelflow.api.service.FuelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/fuel")
public class FuelController {

    @Autowired
    private FuelService fuelService;

    @GetMapping("municipality")
    public ResponseEntity<Page<FuelStationResponse>> getAllByMunicipality(
            @RequestParam String municipality, @RequestParam String state,
            @RequestParam int pageNumber) {
        return ResponseEntity.ok(fuelService.getAllByMunicipality(municipality, state, pageNumber));
    }

    @GetMapping("neighborhood")
    public ResponseEntity<Page<FuelStationResponse>> getAllByMunicipality(
            @RequestParam String municipality, @RequestParam String state, @RequestParam String neighborhood,
            @RequestParam int pageNumber) {
        return ResponseEntity.ok(fuelService.getByCnpj(municipality, state, neighborhood, pageNumber));
    }

    @GetMapping("station/{cnpj}")
    public ResponseEntity<FuelStationResponse> getAllByMunicipality(@PathVariable String cnpj) {
        return ResponseEntity.ok(fuelService.getByCnpj(cnpj));
    }

    @GetMapping("municipality/{state}/{municipality}/avg")
    public ResponseEntity<?> getAvgByMunicipality(@PathVariable String state, @PathVariable String municipality) {
        return ResponseEntity.ok(fuelService.getAverage(state, municipality));
    }

    @GetMapping("municipality/{state}/{municipality}/{neighborhood}/avg")
    public ResponseEntity<?> getAvgByMunicipality(@PathVariable String state, @PathVariable String municipality, @PathVariable String neighborhood) {
        return ResponseEntity.ok(fuelService.getAverage(state, municipality, neighborhood));
    }
}
