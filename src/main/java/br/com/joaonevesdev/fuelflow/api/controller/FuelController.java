package br.com.joaonevesdev.fuelflow.api.controller;

import br.com.joaonevesdev.fuelflow.api.model.dto.FuelStationResponse;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelStation;
import br.com.joaonevesdev.fuelflow.api.service.FuelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/fuel")
public class FuelController {

    @Autowired
    private FuelService fuelService;

    @GetMapping("municipality")
    public ResponseEntity<Page<FuelStationResponse>> getAllByMunicipality(
            @RequestParam String municipality, @RequestParam String state,
            @RequestParam int pageNumber, @RequestParam int pageSize) {
        return ResponseEntity.ok(fuelService.getAllByMunicipality(municipality, state, pageNumber, pageSize));
    }


}
