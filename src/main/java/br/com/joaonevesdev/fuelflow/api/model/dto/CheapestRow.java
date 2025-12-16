package br.com.joaonevesdev.fuelflow.api.model.dto;

import java.math.BigDecimal;

public record CheapestRow(
        BigDecimal price,
        String cnpj,
        String name,
        String neighborhood
) {}

