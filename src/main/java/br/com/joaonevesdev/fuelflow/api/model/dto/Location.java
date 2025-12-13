package br.com.joaonevesdev.fuelflow.api.model.dto;

import br.com.joaonevesdev.fuelflow.api.util.StringFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class Location implements Serializable {
    private String state;
    private String city;
    private String neighborhood;

    public Location(String state, String city, String neighborhood) {
        this.state = state;
        this.city = StringFormat.format(city);
        this.neighborhood = neighborhood;
    }

    public Location(String state, String city) {
        this.state = state;
        this.city = StringFormat.format(city);
        this.neighborhood = "Any";
    }
}
