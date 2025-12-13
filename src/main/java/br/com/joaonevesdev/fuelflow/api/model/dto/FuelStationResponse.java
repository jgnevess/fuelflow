package br.com.joaonevesdev.fuelflow.api.model.dto;

import br.com.joaonevesdev.fuelflow.api.model.entity.Address;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelPrice;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelStation;
import br.com.joaonevesdev.fuelflow.api.util.StringFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class FuelStationResponse {
    private String cnpj;
    private String name;
    private String brand;
    private boolean active;
    private String fullAddress;
    private List<FuelPriceResponse> historyPrices;
    private Map<String, BigDecimal> latestPrices;

    public FuelStationResponse(FuelStation fuelStation) {
        this.cnpj = fuelStation.getCnpj();
        this.name = StringFormat.format(fuelStation.getName());
        this.brand = StringFormat.format(fuelStation.getBrand());
        this.active = fuelStation.getActive();
        this.historyPrices = fuelStation.getPrices().stream().map(FuelPriceResponse::new).toList();
        this.latestPrices = new HashMap<>();
        this.latestPrices = fuelStation.getPrices().stream()
                .collect(Collectors.groupingBy(
                        FuelPrice::getProduct,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(FuelPrice::getCollectionDate)),
                                optional -> optional.map(FuelPrice::getSalePrice).orElse(null)
                        )
                ));



        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(fuelStation.getAddress().getStreet())
                .append(", ")
                .append(fuelStation.getNumber().equals("S/N") ? fuelStation.getComplement() : fuelStation.getNumber())
                .append(", ")
                .append(fuelStation.getAddress().getNeighborhood())
                .append(", ")
                .append(fuelStation.getAddress().getMunicipality())
                .append(", ")
                .append(fuelStation.getAddress().getState());
        this.fullAddress = StringFormat.format(stringBuilder.toString());


    }

}
