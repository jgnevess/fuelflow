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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public class FuelPriceRepositoryIT {


    @Autowired
    FuelStationRepository fuelStationRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    FuelPriceRepository fuelPriceRepository;

    private Long addressId;
    private final String street = "Rua 1";
    private final String neighborhood = "centro";
    private final String municipality = "araraquara";
    private final String state = "SP";

    @BeforeEach
    void limpaBanco() {
        fuelPriceRepository.deleteAll();
        fuelStationRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        LocalDateTime now = LocalDateTime.now();
        Address a = new Address(street, neighborhood, municipality, state, "sudeste", "123456789", 0.0, 0.0, now, now);
        a = addressRepository.save(a);
        addressId = a.getId();
        FuelStation fs = new FuelStation("99999999999999", a, "1", "rua do campo", "posto de teste", "branca", "posto do teste ltda", true, now, now);
        fuelStationRepository.save(fs);

        FuelPrice fp = new FuelPrice(fs, LocalDate.of(2025, 12, 30), "etanol", BigDecimal.valueOf(3.89), null, "R$/L", now);
        fuelPriceRepository.save(fp);
    }


    @Test
    @Transactional
    void findLatestByStation() {
        var res = fuelPriceRepository.findLatestByStation("99999999999999");
        assertFalse(res.isEmpty());
        assertEquals(BigDecimal.valueOf(3.89), res.get(0).getSalePrice());
    }

    @Test
    @Transactional
    void findLatestByStationNotExists() {
        var res = fuelPriceRepository.findLatestByStation("1234567890");
        assertTrue(res.isEmpty());
    }

    @Test
    @Transactional
    void findByCity() {
        var res = fuelPriceRepository.findByCity(municipality, state);
        assertFalse(res.isEmpty());
        assertEquals(BigDecimal.valueOf(3.89), res.get(0).getSalePrice());
    }

    @Test
    @Transactional
    void findByCityNotExists() {
        var res = fuelPriceRepository.findByCity("São Carlos", state);
        assertTrue(res.isEmpty());
    }

    @Test
    @Transactional
    void findByCityAndStateNotExists() {
        var res = fuelPriceRepository.findByCity(municipality, "RJ");
        assertTrue(res.isEmpty());
    }

    @Test
    @Transactional
    void findByNeighborhood() {
        var res = fuelPriceRepository.findByNeighborhood(municipality, state, neighborhood);
        assertFalse(res.isEmpty());
        assertEquals(BigDecimal.valueOf(3.89), res.get(0).getSalePrice());
    }

    @Test
    @Transactional
    void findByNeighborhoodNotExists() {
        var res = fuelPriceRepository.findByNeighborhood(municipality, state, "Santana");
        assertTrue(res.isEmpty());
    }

    @Test
    @Transactional
    void findByNeighborhoodAndStateNotExists() {
        var res = fuelPriceRepository.findByNeighborhood(municipality, "RJ", neighborhood);
        assertTrue(res.isEmpty());
    }

    @Test
    @Transactional
    void findCheapest() {
        var res = fuelPriceRepository.findCheapest(municipality, state, "etanol");
        assertFalse(res.isEmpty());
    }

    @Test
    @Transactional
    void findByCityAndProductFetched() {
        var res = fuelPriceRepository.findByCityAndProductFetched(municipality, state, "etanol");
        assertFalse(res.isEmpty());
    }

}
