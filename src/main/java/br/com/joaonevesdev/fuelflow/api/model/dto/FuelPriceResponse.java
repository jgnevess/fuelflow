package br.com.joaonevesdev.fuelflow.api.model.dto;

import br.com.joaonevesdev.fuelflow.api.model.entity.FuelPrice;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FuelPriceResponse {
    private LocalDate collectionDate;
    private String product;
    private BigDecimal salePrice;
    private String measurementUnit;

    public FuelPriceResponse(FuelPrice fuelPrice) {
        this.collectionDate = fuelPrice.getCollectionDate();
        this.product = fuelPrice.getProduct();
        this.salePrice = fuelPrice.getSalePrice();
        this.measurementUnit = fuelPrice.getMeasurementUnit();
    }
}
