package br.com.joaonevesdev.fuelflow.api.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class Cheapest implements Serializable {

    private String product;
    private BigDecimal cheapestPrice;
    private MinimalFuelStation station;
    private Map<String, String> context;
    private LocalDate referenceDate = LocalDate.now();

}
