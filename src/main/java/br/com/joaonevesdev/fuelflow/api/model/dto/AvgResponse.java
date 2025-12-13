package br.com.joaonevesdev.fuelflow.api.model.dto;

import lombok.Data;

@Data
public class AvgResponse {
    private Location location;
    private PriceAvg averages;

    public AvgResponse(Location location, PriceAvg averages) {
        this.location = location;
        this.averages = averages;
    }
}
