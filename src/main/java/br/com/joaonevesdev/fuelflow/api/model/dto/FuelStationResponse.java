package br.com.joaonevesdev.fuelflow.api.model.dto;

import br.com.joaonevesdev.fuelflow.api.model.entity.Address;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelStation;
import lombok.Data;

import java.util.List;

@Data
public class FuelStationResponse {
    private String name;
    private String brand;
    private boolean active;
    private String fullAddress;
    private List<FuelPriceResponse> prices;

    public FuelStationResponse(FuelStation fuelStation) {
        this.name = fuelStation.getName();
        this.brand = fuelStation.getBrand();
        this.active = fuelStation.getActive();
        this.prices = fuelStation.getPrices().stream().map(FuelPriceResponse::new).toList();
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
        this.fullAddress = stringBuilder.toString();


    }

}
