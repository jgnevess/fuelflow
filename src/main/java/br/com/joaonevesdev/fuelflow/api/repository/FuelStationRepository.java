package br.com.joaonevesdev.fuelflow.api.repository;

import br.com.joaonevesdev.fuelflow.api.model.FuelStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuelStationRepository extends JpaRepository<FuelStation, String> {

    Optional<FuelStation> findByCnpj(String cnpj);

    List<FuelStation> findByAddressMunicipality(String municipality);

    List<FuelStation> findByAddressMunicipalityAndAddressState(String municipality, String state);

    List<FuelStation> findByAddressState(String state);

    List<FuelStation> findByBrand(String brand);

    @Query("SELECT s FROM FuelStation s WHERE s.address.id = :addressId")
    List<FuelStation> findByAddressId(@Param("addressId") Long addressId);

    @Query("SELECT COUNT(s) FROM FuelStation s WHERE s.address.municipality = :municipality")
    Long countByMunicipality(@Param("municipality") String municipality);
}
