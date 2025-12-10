package br.com.joaonevesdev.fuelflow.api.controller;

import br.com.joaonevesdev.fuelflow.api.repository.AddressRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelPriceRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelStationRepository;
import br.com.joaonevesdev.fuelflow.api.service.CsvImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/import")
@RequiredArgsConstructor
@Tag(name = "Import", description = "Importação de dados CSV")
public class TestController {

    private final CsvImportService csvImportService;
    private final AddressRepository addressRepository;
    private final FuelStationRepository stationRepository;
    private final FuelPriceRepository priceRepository;

    @PostMapping("/test")
    @Operation(summary = "Importar dados de teste do CSV")
    public ResponseEntity<Map<String, Object>> importTestData(
            @Parameter(description = "Número máximo de linhas a importar", example = "10")
            @RequestParam(defaultValue = "10") int maxLines) {

        Map<String, Object> response = new HashMap<>();

        try {
            CsvImportService.ImportResult result = csvImportService.importTestFile(maxLines);

            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("linesProcessed", result.getLinesProcessed());
            response.put("stationsCreated", result.getStationsCreated());
            response.put("pricesCreated", result.getPricesCreated());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/test/clear")
    @Operation(summary = "Limpar todos os dados de teste")
    public ResponseEntity<Map<String, Object>> clearTestData() {
        Map<String, Object> response = new HashMap<>();

        try {
            csvImportService.clearTestData();
            response.put("success", true);
            response.put("message", "Dados de teste limpos com sucesso");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/test/stats")
    @Operation(summary = "Estatísticas dos dados importados")
    public ResponseEntity<Map<String, Object>> getImportStats() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Usando os repositórios diretamente
            long addressCount = addressRepository.count();
            long stationCount = stationRepository.count();
            long priceCount = priceRepository.count();

            response.put("success", true);
            response.put("addresses", addressCount);
            response.put("stations", stationCount);
            response.put("prices", priceCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}