package br.com.joaonevesdev.fuelflow.api.repositories;

import br.com.joaonevesdev.fuelflow.api.TestcontainersConfiguration;
import br.com.joaonevesdev.fuelflow.api.model.entity.Address;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelPrice;
import br.com.joaonevesdev.fuelflow.api.model.entity.FuelStation;
import br.com.joaonevesdev.fuelflow.api.repository.AddressRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelPriceRepository;
import br.com.joaonevesdev.fuelflow.api.repository.FuelStationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public class FuelStationRepositoryIT {

    @Autowired
    FuelStationRepository fuelStationRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    FuelPriceRepository fuelPriceRepository;

    private Long addressId;

    @BeforeEach
    void limpaBanco() {
        fuelPriceRepository.deleteAll();
        fuelStationRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        LocalDateTime now = LocalDateTime.now();
        Address a = new Address("Rua 1", "centro", "araraquara", "SP", "sudeste", "123456789", 0.0, 0.0, now, now);
        a = addressRepository.save(a);
        addressId = a.getId();
        FuelStation fs = new FuelStation("99999999999999", a, "1", "rua do campo", "posto de teste", "branca", "posto do teste ltda", true, now, now);
        fuelStationRepository.save(fs);

        FuelPrice fp = new FuelPrice(fs, LocalDate.of(2025, 12, 30), "etanol", BigDecimal.valueOf(3.89), null, "R$/L", now);
        fuelPriceRepository.save(fp);
    }



    @Test
    @Transactional
    void findByAddressId() {
        List<FuelStation> fuelStation = fuelStationRepository.findByAddressId(addressId);

        assertFalse(fuelStation.isEmpty());
    }

    @Test
    @Transactional
    void countByMunicipality() {
        String municipality = "araraquara";

        Long res = fuelStationRepository.countByMunicipality(municipality);
        assertTrue(res > 0);
    }

    @Test
    @Transactional
    void countByMunicipalityNotExists() {
        String municipality = "cidade_que_nao_existe";

        Long res = fuelStationRepository.countByMunicipality(municipality);
        assertEquals(0, res);
    }
}
