package br.com.joaonevesdev.fuelflow.api.model.dto;

import br.com.joaonevesdev.fuelflow.api.model.entity.FuelStation;
import br.com.joaonevesdev.fuelflow.api.util.StringFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

@Data
public class Cheapest implements Serializable {

    private String product;
    private BigDecimal cheapestPrice;
    private MinimalFuelStation station;
    private Map<String, String> context;
    private LocalDate referenceDate = LocalDate.now();
    private String fullAddress;
    private String mapsLink;

    public void setFullAddress(FuelStation fuelStation) {
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

    public void setMapsLink() {
        String query = URLEncoder.encode(fullAddress, StandardCharsets.UTF_8);
        String link = "https://www.google.com/maps/search/?api=1&query=";
        this.mapsLink = link+query;
    }

}
