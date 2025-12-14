package br.com.joaonevesdev.fuelflow.api.controller;

import br.com.joaonevesdev.fuelflow.api.model.dto.FuelPriceResponse;
import br.com.joaonevesdev.fuelflow.api.model.dto.FuelStationResponse;
import br.com.joaonevesdev.fuelflow.api.service.FuelService;
import br.com.joaonevesdev.fuelflow.api.util.StringNormalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/fuel")
public class FuelController {

    @Autowired
    private FuelService fuelService;
    @Autowired
    private StringNormalizer normalizer;

    @GetMapping("municipality")
    public ResponseEntity<Page<FuelStationResponse>> getAllByMunicipality(
            @RequestParam String municipality, @RequestParam String state,
            @RequestParam int pageNumber) {
        String normalizedMunicipality = normalizer.normalize(municipality);
        String normalizedState = normalizer.normalizeUF(state);
        return ResponseEntity.ok(fuelService.getAllByMunicipality(normalizedMunicipality, normalizedState, pageNumber));
    }

    @GetMapping("neighborhood")
    public ResponseEntity<Page<FuelStationResponse>> getAllByMunicipality(
            @RequestParam String municipality, @RequestParam String state, @RequestParam String neighborhood,
            @RequestParam int pageNumber) {
        String normalizedMunicipality = normalizer.normalize(municipality);
        String normalizedState = normalizer.normalizeUF(state);
        String normalizedNeighborhood = normalizer.normalize(neighborhood);
        return ResponseEntity.ok(fuelService.getByCnpj(normalizedMunicipality, normalizedState, normalizedNeighborhood, pageNumber));
    }

    @GetMapping("station/{cnpj}")
    public ResponseEntity<FuelStationResponse> getAllByMunicipality(@PathVariable String cnpj) {
        return ResponseEntity.ok(fuelService.getByCnpj(cnpj));
    }

    @GetMapping("{state}/{municipality}/avg")
    public ResponseEntity<?> getAvgByMunicipality(@PathVariable String state, @PathVariable String municipality) {
        String normalizedMunicipality = normalizer.normalize(municipality);
        String normalizedState = normalizer.normalizeUF(state);
        return ResponseEntity.ok(fuelService.getAverage(normalizedState, normalizedMunicipality));
    }

    @GetMapping("{state}/{municipality}/{neighborhood}/avg")
    public ResponseEntity<?> getAvgByMunicipality(@PathVariable String state, @PathVariable String municipality, @PathVariable String neighborhood) {
        String normalizedMunicipality = normalizer.normalize(municipality);
        String normalizedState = normalizer.normalizeUF(state);
        String normalizedNeighborhood = normalizer.normalize(neighborhood);
        return ResponseEntity.ok(fuelService.getAverage(normalizedState, normalizedMunicipality, normalizedNeighborhood));
    }

    @GetMapping("{state}/{municipality}/cheapest")
    public ResponseEntity<?> getCheapest(@PathVariable String state, @PathVariable String municipality, @RequestParam String product) {
        String normalizedMunicipality = normalizer.normalize(municipality);
        String normalizedState = normalizer.normalizeUF(state);
        String normalizedProduct = normalizer.normalize(product);
        var response = fuelService.getCheapest(normalizedState, normalizedMunicipality, normalizedProduct);
        if(response == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("{state}/{municipality}/top-prices")
    public ResponseEntity<?> getTopPrices(@PathVariable String state, @PathVariable String municipality, @RequestParam String product) {
        String normalizedMunicipality = normalizer.normalize(municipality);
        String normalizedState = normalizer.normalizeUF(state);
        String normalizedProduct = normalizer.normalize(product);
        var response = fuelService.getTopPrices(normalizedState, normalizedMunicipality, normalizedProduct);
        if(response == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("{state}/{municipality}/worst-prices")
    public ResponseEntity<?> getWorstPrices(@PathVariable String state, @PathVariable String municipality, @RequestParam String product) {
        String normalizedMunicipality = normalizer.normalize(municipality);
        String normalizedState = normalizer.normalizeUF(state);
        String normalizedProduct = normalizer.normalize(product);
        var response = fuelService.getWorstPrices(normalizedState, normalizedMunicipality, normalizedProduct);
        if(response == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(response);
    }
}
