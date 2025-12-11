package br.com.joaonevesdev.fuelflow.api.controller;

import br.com.joaonevesdev.fuelflow.api.model.FuelStation;
import br.com.joaonevesdev.fuelflow.api.service.FuelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/fuel")
public class FuelController {
    @Autowired
    private FuelService fuelService;

    @GetMapping
    public ResponseEntity<List<FuelStation>> getAllByMunicipality(@RequestParam String municipality, @RequestParam String state) {
        return ResponseEntity.ok(fuelService.getAllByMunicipality(municipality, state));
    }
}
