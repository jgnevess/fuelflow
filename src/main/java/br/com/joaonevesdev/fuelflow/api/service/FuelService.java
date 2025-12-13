package br.com.joaonevesdev.fuelflow.api.service;

import br.com.joaonevesdev.fuelflow.api.model.dto.*;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelPrice;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelStation;
import br.com.joaonevesdev.fuelflow.api.repository.FuelPriceRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelStationRepository;
import br.com.joaonevesdev.fuelflow.api.util.StringFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public AvgResponse getAverage(String state, String municipality, String neighborhood) {
        List<FuelPrice> prices = fuelPriceRepository.findByNeighborhood(municipality, state, neighborhood);
        PriceAvg priceAvg = setMapAvg(prices);
        Location location = new Location(state, municipality, neighborhood);
        return new AvgResponse(location, priceAvg);
    }

    public AvgResponse getAverage(String state, String municipality) {
        List<FuelPrice> prices = fuelPriceRepository.findByCity(municipality, state);
        PriceAvg priceAvg = setMapAvg(prices);
        Location location = new Location(state, municipality);
        return new AvgResponse(location, priceAvg);
    }

    public Cheapest getCheapest(String state, String municipality, String product) {
        List<FuelPrice> prices = fuelPriceRepository.findLatestByCity(municipality, state, product);
        FuelStation fuelStation = null;
        BigDecimal cheapest = null;
        for(var p : prices) {
            if(fuelStation == null || cheapest == null || p.getSalePrice().compareTo(cheapest) < 0) {
                fuelStation = p.getStation();
                cheapest = p.getSalePrice();
            }
        }

        Cheapest cheapestResponse = new Cheapest();
        MinimalFuelStation minimalFuelStation = new MinimalFuelStation();
        if (fuelStation == null) {
            return null;
        }
        minimalFuelStation.setCnpj(StringFormat.format(fuelStation.getCnpj()));
        minimalFuelStation.setName(StringFormat.format(fuelStation.getName()));
        minimalFuelStation.setNeighborhood(StringFormat.format(fuelStation.getAddress().getNeighborhood()));
        cheapestResponse.setCheapestPrice(cheapest);
        cheapestResponse.setProduct(StringFormat.format(product));
        cheapestResponse.setStation(minimalFuelStation);
        Map<String, String> context = new HashMap<>();
        context.put("city", StringFormat.format(municipality));
        context.put("state", StringFormat.format(state));
        cheapestResponse.setContext(context);
        return cheapestResponse;
    }

    private PriceAvg setMapAvg(List<FuelPrice> prices) {

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

        return new PriceAvg(gas, gasAd, et, s10, dies, gnv);
    }

    private BigDecimal avg(BigDecimal total, int count) {
        return count == 0
                ? null
                : total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

}
