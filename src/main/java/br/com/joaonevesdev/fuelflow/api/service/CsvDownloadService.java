package br.com.joaonevesdev.fuelflow.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class CsvDownloadService {

    public String csvDownload(String url) {
        try {
            URL urlDownload = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlDownload.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(30_000);
            connection.connect();


            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.warn("No file to download. Server replied HTTP code: {}", connection.getResponseCode());
                return null;
            }
            Path tempFile = Files.createTempFile("anp-", ".csv");

            try (InputStream in = connection.getInputStream();
                 OutputStream out = Files.newOutputStream(tempFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            log.info("File downloaded: {}", tempFile);

            return tempFile.toString();

        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

}
