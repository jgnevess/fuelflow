package br.com.joaonevesdev.fuelflow.api.service;

import br.com.joaonevesdev.fuelflow.api.config.DataConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestDataService {

    private final DataConfig dataConfig;

    @PostConstruct
    public void init() {

        dataConfig.ensureDirectoriesExist();


        Path testFile = dataConfig.getCsv().getTestDirPath().resolve("test_small.csv");

        if (!Files.exists(testFile)) {
            log.info("Arquivo de teste não encontrado: {}", testFile);
            createSampleTestFile();
        }
    }

    private void createSampleTestFile() {
        try {
            Path testDir = dataConfig.getCsv().getTestDirPath();
            Path testFile = testDir.resolve("test_small.csv");

            String csvContent = """
                Regiao - Sigla;Estado - Sigla;Municipio;Revenda;CNPJ da Revenda;Nome da Rua;Numero Rua;Complemento;Bairro;Cep;Produto;Data da Coleta;Valor de Venda;Valor de Compra;Unidade de Medida;Bandeira
                SE;SP;SAO PAULO;POSTO SHELL AEROPORTO; 00.000.000/0001-91;AV DR JUSCELINO KUBITSCHEK DE OLIVEIRA;0001;;VILA OLIMPIA;00000000;GASOLINA;19/01/2024;5,999;;R$ / litro;SHELL
                SE;SP;SAO PAULO;POSTO SHELL AEROPORTO; 00.000.000/0001-91;AV DR JUSCELINO KUBITSCHEK DE OLIVEIRA;0001;;VILA OLIMPIA;00000000;ETANOL;19/01/2024;3,899;;R$ / litro;SHELL
                SE;SP;SAO PAULO;POSTO IPIRANGA PAULISTA; 11.111.111/0001-01;AV PAULISTA;1000;;BELA VISTA;11111111;GASOLINA;19/01/2024;5,899;;R$ / litro;IPIRANGA
                SE;SP;SAO PAULO;POSTO IPIRANGA PAULISTA; 11.111.111/0001-01;AV PAULISTA;1000;;BELA VISTA;11111111;ETANOL;19/01/2024;3,799;;R$ / litro;IPIRANGA
                SE;SP;SAO PAULO;POSTO BR BANDEIRANTES; 22.222.222/0001-02;RUA BANDEIRANTES;500;;VILA MARIANA;22222222;GASOLINA;19/01/2024;5,799;;R$ / litro;BR
                NE;CE;SOBRAL;POSTO RAIZEN; 08.775.979/0002-62;RUA TABELIÃO IDELFONSO CAVALCANTI;455;;CENTRO;62010000;GASOLINA;01/01/2025;6,29;;R$ / litro;RAIZEN
                NE;CE;SOBRAL;POSTO RAIZEN; 08.775.979/0002-62;RUA TABELIÃO IDELFONSO CAVALCANTI;455;;CENTRO;62010000;ETANOL;01/01/2025;5,19;;R$ / litro;RAIZEN
                """;

            Files.writeString(testFile, csvContent);
            log.info("Arquivo de teste criado: {} ({} bytes)",
                    testFile, Files.size(testFile));

        } catch (Exception e) {
            log.error("Falha ao criar arquivo de teste", e);
        }
    }

    public File getTestCsvFile() {
        return dataConfig.getCsv().getTestDirPath()
                .resolve("test_small.csv")
                .toFile();
    }

    public boolean testFileExists() {
        return getTestCsvFile().exists();
    }
}