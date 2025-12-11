package br.com.joaonevesdev.fuelflow.api.service;

import br.com.joaonevesdev.fuelflow.api.model.FuelStation;
import br.com.joaonevesdev.fuelflow.api.repository.FuelPriceRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuelService {

    @Autowired
    private FuelPriceRepository fuelPriceRepository;
    @Autowired
    private FuelStationRepository fuelStationRepository;

    public List<FuelStation> getAllByMunicipality(String municipality, String state) {
        return fuelStationRepository.findByAddressMunicipalityAndAddressState(municipality, state);
    }


}
