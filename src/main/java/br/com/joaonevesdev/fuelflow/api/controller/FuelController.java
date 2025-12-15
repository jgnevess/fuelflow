package br.com.joaonevesdev.fuelflow.api.controller;

import br.com.joaonevesdev.fuelflow.api.model.dto.AvgResponse;
import br.com.joaonevesdev.fuelflow.api.model.dto.Cheapest;
import br.com.joaonevesdev.fuelflow.api.model.dto.FuelPriceResponse;
import br.com.joaonevesdev.fuelflow.api.model.dto.FuelStationResponse;
import br.com.joaonevesdev.fuelflow.api.service.FuelService;
import br.com.joaonevesdev.fuelflow.api.util.StringNormalizer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(
        name = "Fuel",
        description = "Consulta de preços de combustíveis por localização. " +
                "Os dados são provenientes de bases públicas, tratados e armazenados previamente. " +
                "Este serviço é somente leitura."
)
@RestController
@RequestMapping("v1/fuel")
public class FuelController {

    @Autowired
    private FuelService fuelService;
    @Autowired
    private StringNormalizer normalizer;

    @Operation(
            summary = "Lista postos por município",
            description = "Retorna uma lista paginada de postos de combustível com os preços mais recentes"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Postos encontrados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FuelStationResponse.class)
                    )
            )
    })
    @GetMapping("city")
    public ResponseEntity<Page<FuelStationResponse>> getAllByMunicipality(
            @RequestParam String city, @RequestParam String state,
            @RequestParam int pageNumber) {
        String normalizedMunicipality = normalizer.normalize(city);
        String normalizedState = normalizer.normalizeUF(state);
        return ResponseEntity.ok(fuelService.getAllByMunicipality(normalizedMunicipality, normalizedState, pageNumber));
    }

    @Operation(
            summary = "Lista postos por bairro",
            description = "Retorna uma lista paginada de postos de combustível com os preços mais recentes"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Postos encontrados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FuelStationResponse.class)
                    )
            )
    })
    @GetMapping("neighborhood")
    public ResponseEntity<Page<FuelStationResponse>> getAllByMunicipality(
            @RequestParam String city, @RequestParam String state, @RequestParam String neighborhood,
            @RequestParam int pageNumber) {
        String normalizedMunicipality = normalizer.normalize(city);
        String normalizedState = normalizer.normalizeUF(state);
        String normalizedNeighborhood = normalizer.normalize(neighborhood);
        return ResponseEntity.ok(fuelService.getByCnpj(normalizedMunicipality, normalizedState, normalizedNeighborhood, pageNumber));
    }

    @Operation(
            summary = "Busca posto pelo CNPJ",
            description = "Retorna os dados do posto de combustível com base no CNPJ informado"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Posto encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FuelStationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Posto não encontrado"
            )
    })
    @GetMapping("station/{cnpj}")
    public ResponseEntity<FuelStationResponse> getAllByMunicipality(@PathVariable String cnpj) {
        var res = fuelService.getByCnpj(cnpj);
        if (res == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Média de preços por cidade",
            description = "Retorna a média dos preços de combustíveis por cidade e estado. " +
                    "Os dados são consolidados previamente e cacheados para melhor performance."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Média calculada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AvgResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Localização não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("{state}/{city}/avg")
    public ResponseEntity<?> getAvgByMunicipality(@PathVariable String state, @PathVariable String city) {
        String normalizedMunicipality = normalizer.normalize(city);
        String normalizedState = normalizer.normalizeUF(state);
        var res = fuelService.getAverage(normalizedState, normalizedMunicipality);
        if (res == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Média de preços por bairro e cidade",
            description = "Retorna a média dos preços de combustíveis por bairro, cidade e estado. " +
                    "Os dados são consolidados previamente e cacheados para melhor performance."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Média calculada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AvgResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Localização não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("{state}/{city}/{neighborhood}/avg")
    public ResponseEntity<?> getAvgByMunicipality(@PathVariable String state, @PathVariable String city, @PathVariable String neighborhood) {
        String normalizedMunicipality = normalizer.normalize(city);
        String normalizedState = normalizer.normalizeUF(state);
        String normalizedNeighborhood = normalizer.normalize(neighborhood);
        var res = fuelService.getAverage(normalizedState, normalizedMunicipality, normalizedNeighborhood);
        if(res == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Posto com o preço mais barato da cidade",
            description = "Retorna cnpj, endereço completo e o valor do combustivel"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Cheapest.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Localização não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("{state}/{city}/cheapest")
    public ResponseEntity<?> getCheapest(@PathVariable String state, @PathVariable String city, @RequestParam String product) {
        String normalizedMunicipality = normalizer.normalize(city);
        String normalizedState = normalizer.normalizeUF(state);
        String normalizedProduct = normalizer.normalize(product);
        var response = fuelService.getCheapest(normalizedState, normalizedMunicipality, normalizedProduct);
        if(response == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Top 3 Melhores preços",
            description = "Retorna os 3 melhores preços da cidade escolhida"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AvgResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Localização não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("{state}/{city}/top-prices")
    public ResponseEntity<?> getTopPrices(@PathVariable String state, @PathVariable String city, @RequestParam String product) {
        String normalizedMunicipality = normalizer.normalize(city);
        String normalizedState = normalizer.normalizeUF(state);
        String normalizedProduct = normalizer.normalize(product);
        var response = fuelService.getTopPrices(normalizedState, normalizedMunicipality, normalizedProduct);
        if(response == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Top 3 piores preços",
            description = "Retorna os 3 piores preços da cidade escolhida"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AvgResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Localização não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("{state}/{city}/worst-prices")
    public ResponseEntity<?> getWorstPrices(@PathVariable String state, @PathVariable String city, @RequestParam String product) {
        String normalizedMunicipality = normalizer.normalize(city);
        String normalizedState = normalizer.normalizeUF(state);
        String normalizedProduct = normalizer.normalize(product);
        var response = fuelService.getWorstPrices(normalizedState, normalizedMunicipality, normalizedProduct);
        if(response == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(response);
    }
}
