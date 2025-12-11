package br.com.joaonevesdev.fuelflow.api.service;

import br.com.joaonevesdev.fuelflow.api.model.dto.FuelPriceResponse;
import br.com.joaonevesdev.fuelflow.api.model.dto.FuelStationResponse;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelStation;
import br.com.joaonevesdev.fuelflow.api.repository.FuelPriceRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuelService {

    @Autowired
    private FuelPriceRepository fuelPriceRepository;
    @Autowired
    private FuelStationRepository fuelStationRepository;

    public Page<FuelStationResponse> getAllByMunicipality(String municipality, String state, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FuelStationResponse> res = fuelStationRepository.findByAddressMunicipalityAndAddressState(pageable, municipality, state)
                .map(p -> new FuelStationResponse(p));

        return res;
    }


}
