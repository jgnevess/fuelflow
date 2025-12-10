package br.com.joaonevesdev.fuelflow.api.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Configuration
@ConfigurationProperties(prefix = "app.data")
public class DataConfig {

    private String baseDir = "./data";
    private Csv csv = new Csv();
    private Zip zip = new Zip();

    @Getter
    public static class Csv {
        private String testDir = "./data/csv/test";
        private String importDir = "./data/csv/import";

        public Path getTestDirPath() {
            return Paths.get(testDir);
        }

        public Path getImportDirPath() {
            return Paths.get(importDir);
        }
    }

    @Getter
    public static class Zip {
        private String dir = "./data/zip";

        public Path getDirPath() {
            return Paths.get(dir);
        }
    }

    public Path getBaseDirPath() {
        return Paths.get(baseDir);
    }


    public void ensureDirectoriesExist() {
        try {
            java.nio.file.Files.createDirectories(getBaseDirPath());
            java.nio.file.Files.createDirectories(csv.getTestDirPath());
            java.nio.file.Files.createDirectories(csv.getImportDirPath());
            java.nio.file.Files.createDirectories(zip.getDirPath());
        } catch (Exception e) {
            throw new RuntimeException("Falha ao criar diretórios de dados", e);
        }
    }
}