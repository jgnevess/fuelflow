package br.com.joaonevesdev.fuelflow.api.controller;

import br.com.joaonevesdev.fuelflow.api.repository.AddressRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelPriceRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelStationRepository;
import br.com.joaonevesdev.fuelflow.api.service.CsvImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/import")
@RequiredArgsConstructor
@Tag(name = "Import", description = "Importação de dados CSV")
public class DataController {

    private final CsvImportService csvImportService;
    private final AddressRepository addressRepository;
    private final FuelStationRepository stationRepository;
    private final FuelPriceRepository priceRepository;

    @PostMapping(value = "/upload-csv",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Importar dados de teste do CSV")

    public ResponseEntity<Map<String, Object>> importData(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("error", "Arquivo vazio!");
            return ResponseEntity.badRequest().body(response);
        }

        Path dest = Paths.get("/tmp/uploads/data_csv.csv");

        Files.createDirectories(dest.getParent());

        file.transferTo(dest.toFile());

        try {
            CsvImportService.ImportResult result = csvImportService.ImportFile(dest);

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

//    @DeleteMapping("/test/clear")
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