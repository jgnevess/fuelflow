package br.com.joaonevesdev.fuelflow.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Component
public class Worker {

    @Autowired
    private CsvDownloadService csvDownloadService;
    @Autowired
    private CsvImportService csvImportService;

//    @Scheduled(fixedRate = 360000) // teste
    @Scheduled(cron = "0 0 6 * * 4") // todas quintas as 6 a.m.
    public void runServices() throws IOException {
        String[] urls = {
                "https://www.gov.br/anp/pt-br/centrais-de-conteudo/dados-abertos/arquivos/shpc/qus/ultimas-4-semanas-gasolina-etanol.csv",
                "https://www.gov.br/anp/pt-br/centrais-de-conteudo/dados-abertos/arquivos/shpc/qus/ultimas-4-semanas-diesel-gnv.csv"
        };

        for(String url : urls) {
            String res = csvDownloadService.csvDownload(url);
            if (res == null) continue;
            log.info("Iniciando importação do CSV: {}", res);
            Path path = Path.of(res);
            csvImportService.importFile(path);
            log.info("Importação finalizada.");
        }
    }
}
