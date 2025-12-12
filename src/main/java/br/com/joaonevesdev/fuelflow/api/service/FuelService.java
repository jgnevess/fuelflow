package br.com.joaonevesdev.fuelflow.api.service;

import br.com.joaonevesdev.fuelflow.api.model.dto.FuelStationResponse;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelPrice;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelStation;
import br.com.joaonevesdev.fuelflow.api.repository.FuelPriceRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, BigDecimal> getAverage(String state, String municipality) {
        List<FuelPrice> prices = fuelPriceRepository.findByCity(municipality, state);
        Map<String, BigDecimal> response = new HashMap<>();

        BigDecimal gas = new BigDecimal(0);
        int gasi = 0;

        BigDecimal gasAd = new BigDecimal(0);
        int gasAdi = 0;

        BigDecimal et = new BigDecimal(0);
        int eti = 0;

        BigDecimal s10 = new BigDecimal(0);
        int s10i = 0;

        BigDecimal dies = new BigDecimal(0);
        int diesi = 0;

        BigDecimal gnv = new BigDecimal(0);
        int gnvi = 0;

        for (FuelPrice f : prices) {
            switch (f.getProduct()) {
                case "GASOLINA":
                    gasi++;
                    gas = gas.add(f.getSalePrice());
                    break;

                case "ETANOL":
                    eti++;
                    et = et.add(f.getSalePrice());
                    break;

                case "GASOLINA ADITIVADA":
                    gasAdi++;
                    gasAd = gasAd.add(f.getSalePrice());
                    break;
                case "DIESEL S10":
                    s10i++;
                    s10 = s10.add(f.getSalePrice());
                    break;
                case "GNV":
                    gnvi++;
                    gnv = gnv.add(f.getSalePrice());
                    break;
                case "DIESEL":
                    diesi++;
                    dies = dies.add(f.getSalePrice());
                    break;
            }

        }

        gas = avg(gas, gasi);
        gasAd = avg(gasAd, gasAdi);
        et = avg(et, eti);
        dies = avg(dies, diesi);
        s10 = avg(s10, s10i);
        gnv = avg(gnv, gnvi);

        response.put("GASOLINA", gas);
        response.put("GASOLINA ADITIVADA", gasAd);
        response.put("ETANOL", et);
        response.put("DIESEL", dies);
        response.put("DIESEL S10", s10);
        response.put("GNV", gnv);

        return response;
    }

    private BigDecimal avg(BigDecimal total, int count) {
        return count == 0
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }


}
