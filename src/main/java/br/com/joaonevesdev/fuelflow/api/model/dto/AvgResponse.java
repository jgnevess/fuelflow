package br.com.joaonevesdev.fuelflow.api.model.dto;

import lombok.Data;

@Data
public class AvgResponse<T> {
    private Location location;
    private T results;
    private String context;

    public AvgResponse(Location location, T results, String context) {
        this.location = location;
        this.results = results;
        this.context = context;
    }
}
