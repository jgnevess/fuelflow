package br.com.joaonevesdev.fuelflow.api.batch;

import br.com.joaonevesdev.fuelflow.api.model.entity.Address;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelPrice;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelStation;
import br.com.joaonevesdev.fuelflow.api.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvRecordProcessor {

    @Autowired
    private final AddressRepository addressRepository;

    private final Map<String, Address> addressCache = new HashMap<>();

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ProcessingResult processRecord(CSVRecord record) {
        try {
            String rawCnpj = record.get("CNPJ da Revenda");
            String cnpj = FuelStation.cleanCnpj(rawCnpj);

            if (cnpj == null || cnpj.length() != 14) {
                log.warn("CNPJ inválido ou ausente: '{}'", rawCnpj);
                return null;
            }

            String street = getValue(record, "Nome da Rua");
            String neighborhood = getValue(record, "Bairro");
            String municipality = getValue(record, "Municipio");
            String state = getValue(record, "Estado - Sigla");
            String region = record.toMap().entrySet().stream()
                    .filter(e -> e.getKey().toLowerCase().contains("regiao"))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);;
            String cep = getValue(record, "Cep");

            if (street == null || municipality == null || state == null) {
                log.warn("Campos obrigatórios ausentes na linha {}", record.getRecordNumber());
                return null;
            }

            Address address = getOrCreateAddress(street, neighborhood,
                    municipality, state, region, cep);

            if (address == null) {
                return null;
            }

            FuelStation station = FuelStation.fromCsvRecord(
                    cnpj,
                    address,
                    getValue(record, "Numero Rua"),
                    getValue(record, "Complemento"),
                    getValue(record, "Revenda"),
                    getValue(record, "Bandeira")
            );

            String dateStr = getValue(record, "Data da Coleta");
            LocalDate collectionDate = parseDate(dateStr);
            if (collectionDate == null) {
                log.warn("Data inválida na linha {}: '{}'", record.getRecordNumber(), dateStr);
                return null;
            }

            FuelPrice price = FuelPrice.fromCsvRecord(
                    station,
                    collectionDate,
                    getValue(record, "Produto"),
                    getValue(record, "Valor de Venda"),
                    getValue(record, "Valor de Compra"),
                    getValue(record, "Unidade de Medida")
            );

            return new ProcessingResult(address, station, price);

        } catch (Exception e) {
            log.warn("Erro ao processar linha {}: {}",
                    record.getRecordNumber(), e.getMessage());
            return null;
        }
    }

    private Address getOrCreateAddress(String street, String neighborhood,
                                       String municipality, String state,
                                       String region, String cep) {
        String hash = Address.generateHash(street, neighborhood, municipality, state, cep);

        Address address = addressCache.get(hash);
        if (address != null) {
            return address;
        }

        address = addressRepository.findByHash(hash).orElse(null);

        if (address == null) {
            address = Address.fromCsvRecord(street, neighborhood, municipality,
                    state, region, cep);
            address.setHash(hash);

            addressCache.put(hash, address);

            log.debug("Novo endereço criado (hash: {}): {} - {}",
                    hash.substring(0, 8), street, municipality);
        }

        return address;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (Exception e) {
            log.debug("Falha ao parsear data '{}': {}", dateStr, e.getMessage());
            return null;
        }
    }

    private String getValue(CSVRecord record, String columnName) {
        try {
            String value = record.get(columnName);
            return value != null ? value.trim() : null;
        } catch (IllegalArgumentException e) {
            // Coluna não encontrada
            return null;
        }
    }

    public void clearCache() {
        addressCache.clear();
        log.debug("Cache de endereços limpo");
    }

    public static class ProcessingResult {
        public final Address address;
        public final FuelStation station;
        public final FuelPrice price;

        public ProcessingResult(Address address, FuelStation station, FuelPrice price) {
            this.address = address;
            this.station = station;
            this.price = price;
        }
    }
}
