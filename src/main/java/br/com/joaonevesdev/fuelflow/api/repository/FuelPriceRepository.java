package br.com.joaonevesdev.fuelflow.api.repository;

import br.com.joaonevesdev.fuelflow.api.model.entity.FuelPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FuelPriceRepository extends JpaRepository<FuelPrice, Long> {

    List<FuelPrice> findByStationCnpj(String stationCnpj);

    List<FuelPrice> findByStationCnpjAndProduct(String stationCnpj, String product);

    List<FuelPrice> findByStationCnpjAndCollectionDateBetween(
            String stationCnpj, LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM FuelPrice p WHERE p.collectionDate = " +
            "(SELECT MAX(p2.collectionDate) FROM FuelPrice p2 WHERE p2.station.cnpj = :cnpj) " +
            "AND p.station.cnpj = :cnpj")
    List<FuelPrice> findLatestByStation(@Param("cnpj") String cnpj);

    @Query("SELECT p FROM FuelPrice p WHERE " +
            "p.station.address.municipality = :municipality AND " +
            "p.station.address.state = :state AND " +
            "p.collectionDate = :date")
    List<FuelPrice> findByCityAndDate(
            @Param("municipality") String municipality,
            @Param("state") String state,
            @Param("date") LocalDate date);

    @Query("SELECT p FROM FuelPrice p WHERE " +
            "p.station.address.municipality = :municipality AND " +
            "p.station.address.state = :state")
    List<FuelPrice> findByCity(
            @Param("municipality") String municipality,
            @Param("state") String state);

    @Query("SELECT p FROM FuelPrice p WHERE " +
            "p.station.address.municipality = :municipality AND " +
            "p.station.address.state = :state AND " +
            "p.station.address.neighborhood = :neighborhood")
    List<FuelPrice> findByNeighborhood(
            @Param("municipality") String municipality,
            @Param("state") String state,
            @Param("neighborhood") String neighborhood);

    @Query("""
            SELECT fp
            FROM FuelPrice fp
            WHERE fp.product = :product
              AND  fp.station.address.municipality = :municipality
              AND fp.station.address.state = :state
              AND fp.collectionDate = (
                  SELECT MAX(fp2.collectionDate)
                  FROM FuelPrice fp2
                  WHERE fp2.station = fp.station
                    AND fp2.product = fp.product
              )
            """)
    List<FuelPrice> findLatestByCity(
            @Param("municipality") String municipality,
            @Param("state") String state,
            @Param("product") String product
    );

    @Query("""
            SELECT fp
            FROM FuelPrice fp
            WHERE fp.product = :product
              AND  fp.station.address.municipality = :municipality
              AND fp.station.address.state = :state
            """)
    List<FuelPrice> findByCityAndProduct(
            @Param("municipality") String municipality,
            @Param("state") String state,
            @Param("product") String product
    );

}