package br.com.joaonevesdev.fuelflow.api.repository;

import br.com.joaonevesdev.fuelflow.api.model.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByHash(String hash);

    List<Address> findByMunicipalityAndState(String municipality, String state);

    List<Address> findByState(String state);

    @Query("SELECT a FROM Address a WHERE " +
            "a.street = :street AND " +
            "a.neighborhood = :neighborhood AND " +
            "a.municipality = :municipality AND " +
            "a.state = :state")
    Optional<Address> findByCompleteAddress(
            @Param("street") String street,
            @Param("neighborhood") String neighborhood,
            @Param("municipality") String municipality,
            @Param("state") String state);
}
