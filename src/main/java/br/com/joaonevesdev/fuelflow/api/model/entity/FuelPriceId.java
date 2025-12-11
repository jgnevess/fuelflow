package br.com.joaonevesdev.fuelflow.api.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
class FuelPriceId implements java.io.Serializable {
    private String station;
    private LocalDate collectionDate;
    private String product;
}
