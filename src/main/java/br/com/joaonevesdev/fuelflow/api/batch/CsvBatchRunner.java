package br.com.joaonevesdev.fuelflow.api.batch;

import br.com.joaonevesdev.fuelflow.api.service.CsvImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Component
public class CsvBatchRunner implements CommandLineRunner {

    private final CsvImportService csvImportService;

    public CsvBatchRunner(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @Override
    public void run(String... args) throws IOException {

        if (args.length < 2) return;
        if (!"import".equalsIgnoreCase(args[0])) return;

        String csvPath = args[1];

        log.info("Iniciando importação do CSV: {}", csvPath);
        Path path = Path.of(csvPath);
        csvImportService.importFile(path);
        log.info("Importação finalizada.");
        System.exit(0);
    }
}

