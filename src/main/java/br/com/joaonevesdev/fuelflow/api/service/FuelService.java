package br.com.joaonevesdev.fuelflow.api.service;

import br.com.joaonevesdev.fuelflow.api.model.dto.FuelStationResponse;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelStation;
import br.com.joaonevesdev.fuelflow.api.repository.FuelPriceRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FuelService {

    @Autowired
    private FuelPriceRepository fuelPriceRepository;
    @Autowired
    private FuelStationRepository fuelStationRepository;

    public Page<FuelStationResponse> getAllByMunicipality(String municipality, String state, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        return fuelStationRepository.findByAddressMunicipalityAndAddressState(pageable, municipality, state)
                .map(FuelStationResponse::new);
    }

    public Page<FuelStationResponse> getByCnpj(String municipality, String state, String neighborhood, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        return fuelStationRepository.findByAddressMunicipalityAndAddressStateAndAddressNeighborhood(pageable, municipality, state, neighborhood)
                .map(FuelStationResponse::new);
    }

    public FuelStationResponse getByCnpj(String cnpj) {
        FuelStation fuelStation = fuelStationRepository.findByCnpj(cnpj).orElseThrow();
        return new FuelStationResponse(fuelStation);
    }

}
