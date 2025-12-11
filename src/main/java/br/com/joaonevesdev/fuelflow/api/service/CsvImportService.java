package br.com.joaonevesdev.fuelflow.api.service;

import br.com.joaonevesdev.fuelflow.api.batch.CsvRecordProcessor;
import br.com.joaonevesdev.fuelflow.api.model.Address;
import br.com.joaonevesdev.fuelflow.api.model.FuelStation;
import br.com.joaonevesdev.fuelflow.api.repository.AddressRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelPriceRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvImportService {

    private final AddressRepository addressRepository;
    private final FuelStationRepository stationRepository;
    private final FuelPriceRepository priceRepository;
    private final CsvRecordProcessor csvRecordProcessor;

    public ImportResult ImportFile(Path dest) throws IOException {
        long maxLines = Files.lines(dest).count();

        try {
            File file = new File(dest.toUri());

            if (!file.exists()) {
                return ImportResult.error("Arquivo de teste não encontrado: " + file.getAbsolutePath());
            }

            log.info("Iniciando importação de teste (max {} linhas) do arquivo: {}",
                    maxLines, file.getName());

            int linesProcessed = 0;
            int stationsCreated = 0;
            int pricesCreated = 0;

            try (Reader reader = new FileReader(file, StandardCharsets.ISO_8859_1);
                 CSVParser csvParser = CSVFormat.DEFAULT
                         .withDelimiter(';')
                         .withFirstRecordAsHeader()
                         .withIgnoreHeaderCase()
                         .withTrim()
                         .parse(reader)) {

                int limit = 0;
                for (CSVRecord record : csvParser) {
                    if (linesProcessed >= maxLines) {
                        log.info("Limite de {} linhas atingido", maxLines);
                        break;
                    }

                    try {
                        CsvRecordProcessor.ProcessingResult result =
                                csvRecordProcessor.processRecord(record);

                        if (result != null) {
                            boolean createdStation = false;

                            createdStation = saveLine(result);

                            linesProcessed++;

                            if (createdStation) {
                                stationsCreated++;
                            }

                            pricesCreated++;

                            if (linesProcessed % 10 == 0) {
                                log.info("Processadas {} linhas...", linesProcessed);
                            }

                            if (limit == 500) {
                                limit = 0;
                                Thread.sleep(500);
                            } else {
                                limit++;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean saveLine(CsvRecordProcessor.ProcessingResult r) {

        Address address = addressRepository.findByHash(r.address.getHash())
                .orElseGet(() -> addressRepository.save(r.address));


        boolean createdStation = false;

        FuelStation station = stationRepository.findByCnpj(r.station.getCnpj()).orElse(null);

        if (station == null) {
            station = r.station;
            station.setAddress(address); // garantir FK
            stationRepository.save(station);
            createdStation = true;
        }

        r.price.setStation(station);
        priceRepository.save(r.price);

        return createdStation;
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