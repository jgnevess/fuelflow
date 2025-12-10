package br.com.joaonevesdev.fuelflow.api.service;

import br.com.joaonevesdev.fuelflow.api.batch.CsvRecordProcessor;
import br.com.joaonevesdev.fuelflow.api.repository.AddressRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelPriceRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvImportService {

    private final AddressRepository addressRepository;
    private final FuelStationRepository stationRepository;
    private final FuelPriceRepository priceRepository;
    private final CsvRecordProcessor csvRecordProcessor;

    @Transactional
    public ImportResult importTestFile(int maxLines) {
        try {
            File testFile = new File("./data/csv/test/test_total.csv");

            if (!testFile.exists()) {
                return ImportResult.error("Arquivo de teste não encontrado: " + testFile.getAbsolutePath());
            }

            log.info("Iniciando importação de teste (max {} linhas) do arquivo: {}",
                    maxLines, testFile.getName());

            int linesProcessed = 0;
            int stationsCreated = 0;
            int pricesCreated = 0;

            try (Reader reader = new FileReader(testFile, StandardCharsets.ISO_8859_1);
                 CSVParser csvParser = CSVFormat.DEFAULT
                         .withDelimiter(';')
                         .withFirstRecordAsHeader()
                         .withIgnoreHeaderCase()
                         .withTrim()
                         .parse(reader)) {

                for (CSVRecord record : csvParser) {
                    if (linesProcessed >= maxLines) {
                        log.info("Limite de {} linhas atingido", maxLines);
                        break;
                    }

                    try {
                        CsvRecordProcessor.ProcessingResult result =
                                csvRecordProcessor.processRecord(record);

                        if (result != null) {
                            if (result.address.getId() == null) {
                                addressRepository.save(result.address);
                            }

                            if (!stationRepository.existsById(result.station.getCnpj())) {
                                stationRepository.save(result.station);
                                stationsCreated++;
                            }

                            priceRepository.save(result.price);
                            pricesCreated++;

                            linesProcessed++;

                            if (linesProcessed % 10 == 0) {
                                log.info("Processadas {} linhas...", linesProcessed);
                            }
                        }

                    } catch (Exception e) {
                        log.warn("Erro ao processar linha {}: {}",
                                record.getRecordNumber(), e.getMessage());
                    }
                }

                log.info("Importação de teste concluída: {} linhas, {} postos, {} preços",
                        linesProcessed, stationsCreated, pricesCreated);

                return ImportResult.success(linesProcessed, stationsCreated, pricesCreated);

            }

        } catch (Exception e) {
            log.error("Erro na importação de teste", e);
            return ImportResult.error(e.getMessage());
        }
    }

    @Transactional
    public void clearTestData() {
        log.info("Limpando dados de teste...");
        priceRepository.deleteAll();
        stationRepository.deleteAll();
        addressRepository.deleteAll();
        log.info("Dados de teste limpos");
    }

    public long getAddressCount() {
        return addressRepository.count();
    }

    public long getStationCount() {
        return stationRepository.count();
    }

    public long getPriceCount() {
        return priceRepository.count();
    }

    public static class ImportResult {
        private final boolean success;
        private final String message;
        private final int linesProcessed;
        private final int stationsCreated;
        private final int pricesCreated;

        private ImportResult(boolean success, String message,
                             int linesProcessed, int stationsCreated, int pricesCreated) {
            this.success = success;
            this.message = message;
            this.linesProcessed = linesProcessed;
            this.stationsCreated = stationsCreated;
            this.pricesCreated = pricesCreated;
        }

        public static ImportResult success(int lines, int stations, int prices) {
            return new ImportResult(true, "Importação bem-sucedida",
                    lines, stations, prices);
        }

        public static ImportResult error(String errorMessage) {
            return new ImportResult(false, errorMessage, 0, 0, 0);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getLinesProcessed() { return linesProcessed; }
        public int getStationsCreated() { return stationsCreated; }
        public int getPricesCreated() { return pricesCreated; }
    }
}