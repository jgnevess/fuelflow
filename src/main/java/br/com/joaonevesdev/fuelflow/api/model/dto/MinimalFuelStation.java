package br.com.joaonevesdev.fuelflow.api.model.dto;

import lombok.Data;

@Data
public class MinimalFuelStation {
    private String cnpj;
    private String name;
    private String neighborhood;
}
