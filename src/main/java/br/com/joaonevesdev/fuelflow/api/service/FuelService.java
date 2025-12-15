package br.com.joaonevesdev.fuelflow.api.service;

import br.com.joaonevesdev.fuelflow.api.model.dto.*;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelPrice;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelStation;
import br.com.joaonevesdev.fuelflow.api.repository.FuelPriceRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelStationRepository;
import br.com.joaonevesdev.fuelflow.api.util.StringFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class FuelService {

    @Autowired
    private FuelPriceRepository fuelPriceRepository;
    @Autowired
    private FuelStationRepository fuelStationRepository;



    public Page<FuelStationResponse> getAllByMunicipality(String city, String state, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        return fuelStationRepository.findByAddressMunicipalityAndAddressState(pageable, city, state)
                .map(FuelStationResponse::new);
    }

    public Page<FuelStationResponse> getByCnpj(String city, String state, String neighborhood, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        return fuelStationRepository.findByAddressMunicipalityAndAddressStateAndAddressNeighborhood(pageable, city, state, neighborhood)
                .map(FuelStationResponse::new);
    }

    @Cacheable(
            value = "station",
            key = "#cnpj"
    )
    public FuelStationResponse getByCnpj(String cnpj) {
        FuelStation fuelStation = fuelStationRepository.findByCnpj(cnpj).orElse(null);
        if (fuelStation == null) return null;
        return new FuelStationResponse(fuelStation);
    }

    @Cacheable(
            value = "avg_by_location_neighborhood",
            key = "#state + ':' + #city + ':' + #neighborhood"
    )
    public AvgResponse getAverage(String state, String city, String neighborhood) {
        List<FuelPrice> prices = fuelPriceRepository.findByNeighborhood(city, state, neighborhood);
        if (prices.isEmpty()) return null;
        PriceAvg priceAvg = setMapAvg(prices);
        Location location = new Location(state, city, neighborhood);
        return new AvgResponse(location, priceAvg, "MEDIA_POR_BAIRRO");
    }

//    @Cacheable(
//            value = "avg_by_location",
//            key = "#state + ':' + #city"
//    )
    public AvgResponse getAverage(String state, String city) {
        log.info("BUSCANDO NO BANCO - Key do cache: addresses::{}", city);
        List<FuelPrice> prices = fuelPriceRepository.findByCity(city, state);
        if (prices.isEmpty()) return null;
        PriceAvg priceAvg = setMapAvg(prices);
        Location location = new Location(state, city);
        return new AvgResponse(location, priceAvg, "MEDIA_POR_CIDADE");
    }

    @Cacheable(
            value = "cheapest_by_location",
            key = "#state + ':' + #city + ':' + #product"
    )
    public Cheapest getCheapest(String state, String city, String product) {
        List<FuelPrice> prices = fuelPriceRepository.findLatestByCity(city, state, product);
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
        context.put("city", StringFormat.format(city));
        context.put("state", StringFormat.format(state));
        cheapestResponse.setContext(context);
        return cheapestResponse;
    }

    @Cacheable(
            value = "top_prices_by_location",
            key = "#state + ':' + #city + ':' + #product"
    )
    public AvgResponse getTopPrices(String state, String city, String product) {
        List<FuelPrice> fuelPrices = fuelPriceRepository.findByCityAndProduct(city, state, product);
        if(fuelPrices.isEmpty()) return null;
        Map<FuelStation, List<FuelPrice>> pricesByStation = fuelPrices.stream()
                .collect(Collectors.groupingBy(FuelPrice::getStation));

        List<StationSummary> stationSummaries = pricesByStation.entrySet().stream()
                .filter(entry -> entry.getValue().size() >= 3)
                .map(entry -> createStationSummary(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(StationSummary::getAveragePrice))
                .limit(3)
                .collect(Collectors.toList());
        Location location = new Location(state, city);
        AvgResponse avgResponse = new AvgResponse<>(location, stationSummaries, "TOP_3_MELHORES_PREÇOS_POR_CIDADE");
        return avgResponse;
    }

    @Cacheable(
            value = "worst_prices_by_location",
            key = "#state + ':' + #city + ':' + #product"
    )
    public AvgResponse getWorstPrices(String state, String city, String product) {
        List<FuelPrice> fuelPrices = fuelPriceRepository.findByCityAndProduct(city, state, product);
        if(fuelPrices.isEmpty()) return null;
        Map<FuelStation, List<FuelPrice>> pricesByStation = fuelPrices.stream()
                .collect(Collectors.groupingBy(FuelPrice::getStation));

        List<StationSummary> stationSummaries = pricesByStation.entrySet().stream()
                .filter(entry -> entry.getValue().size() >= 3)
                .map(entry -> createStationSummary(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(StationSummary::getAveragePrice).reversed())
                .limit(3)
                .collect(Collectors.toList());
        Location location = new Location(state, city);
        AvgResponse avgResponse = new AvgResponse<>(location, stationSummaries, "TOP_3_PIORES_PREÇOS_POR_CIDADE");

        return avgResponse;
    }

    private StationSummary createStationSummary(FuelStation station, List<FuelPrice> prices) {
        List<FuelPrice> sortedPrices = prices.stream()
                .sorted(Comparator.comparing(FuelPrice::getCollectionDate).reversed())
                .collect(Collectors.toList());

        BigDecimal averagePrice = prices.stream()
                .map(FuelPrice::getSalePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(prices.size()), 2, RoundingMode.HALF_UP);

        BigDecimal minPrice = prices.stream()
                .map(FuelPrice::getSalePrice)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal maxPrice = prices.stream()
                .map(FuelPrice::getSalePrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        List<BigDecimal> last3Prices = sortedPrices.stream()
                .limit(3)
                .map(FuelPrice::getSalePrice)
                .collect(Collectors.toList());

        StringBuilder address = new StringBuilder(station.getAddress().getStreet());
        address.append(", ")
                .append(station.getNumber() == null ? station.getComplement() : station.getNumber())
                .append(", ")
                .append(station.getAddress().getMunicipality())
                .append(", ")
                .append(station.getAddress().getState());

        return StationSummary.builder()
                .averagePrice(averagePrice)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .last3Prices(last3Prices)
                .stationName(StringFormat.format(station.getName()))
                .build();
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
            switch (f.getProduct().toUpperCase()) {
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
