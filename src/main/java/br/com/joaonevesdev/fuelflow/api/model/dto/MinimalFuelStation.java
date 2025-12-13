package br.com.joaonevesdev.fuelflow.api.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MinimalFuelStation implements Serializable {
    private String cnpj;
    private String name;
    private String neighborhood;
}
