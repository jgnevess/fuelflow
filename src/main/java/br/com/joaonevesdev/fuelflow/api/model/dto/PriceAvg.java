package br.com.joaonevesdev.fuelflow.api.model.dto;

import br.com.joaonevesdev.fuelflow.api.util.BigDecimalTwoScaleSerializer;

import lombok.Data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


import java.math.BigDecimal;

@Data
public class PriceAvg {

    @JsonSerialize(using = BigDecimalTwoScaleSerializer.class)
    private BigDecimal gasolina;
    @JsonSerialize(using = BigDecimalTwoScaleSerializer.class)
    private BigDecimal gasolinaAditivada;
    @JsonSerialize(using = BigDecimalTwoScaleSerializer.class)
    private BigDecimal etanol;
    @JsonSerialize(using = BigDecimalTwoScaleSerializer.class)
    private BigDecimal dieselS10;
    @JsonSerialize(using = BigDecimalTwoScaleSerializer.class)
    private BigDecimal diesel;
    @JsonSerialize(using = BigDecimalTwoScaleSerializer.class)
    private BigDecimal gnv;

    public PriceAvg(BigDecimal gasolina, BigDecimal gasolinaAditivada, BigDecimal etanol, BigDecimal dieselS10, BigDecimal diesel, BigDecimal gnv) {
        this.gasolina = gasolina;
        this.gasolinaAditivada = gasolinaAditivada;
        this.etanol = etanol;
        this.dieselS10 = dieselS10;
        this.diesel = diesel;
        this.gnv = gnv;
    }
}

