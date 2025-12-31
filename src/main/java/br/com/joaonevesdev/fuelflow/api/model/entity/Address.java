package br.com.joaonevesdev.fuelflow.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_address_unique",
                columnNames = {"street", "neighborhood", "municipality", "state", "cep"}
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String street;

    @Column(length = 100)
    private String neighborhood;

    @Column(nullable = false, length = 100)
    private String municipality;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(length = 20)
    private String region;

    @Column(length = 9)
    private String cep;

    private Double latitude;
    private Double longitude;

    public Address(String street, String neighborhood, String municipality, String state, String region, String cep, Double latitude, Double longitude, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.street = street;
        this.neighborhood = neighborhood;
        this.municipality = municipality;
        this.state = state;
        this.region = region;
        this.cep = cep;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hash = hash;
        this.stations = stations;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static class FuelPriceId {
}    @NaturalId
    @Column(unique = true, length = 32)
    private String hash;

    @JsonIgnore
    @OneToMany(mappedBy = "address", fetch = FetchType.LAZY)
    @Builder.Default
    private List<FuelStation> stations = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public static String generateHash(String street, String neighborhood,
                                      String municipality, String state, String cep) {
        String addressKey = String.format("%s|%s|%s|%s|%s",
                normalize(street),
                normalize(neighborhood),
                normalize(municipality),
                normalize(state),
                normalize(cep)
        );
        return DigestUtils.md5DigestAsHex(addressKey.getBytes());
    }

    private static String normalize(String value) {
        return value != null ? value.trim().toUpperCase() : "";
    }

    @PrePersist
    @PreUpdate
    public void generateHash() {
        this.hash = generateHash(this.street, this.neighborhood,
                this.municipality, this.state, this.cep);
    }

    public static Address fromCsvRecord(String street, String neighborhood,
                                        String municipality, String state,
                                        String region, String cep) {
        return Address.builder()
                .street(street != null ? street.trim().toLowerCase() : null)
                .neighborhood(neighborhood != null ? neighborhood.trim().toLowerCase() : null)
                .municipality(municipality != null ? municipality.trim().toLowerCase() : null)
                .state(state != null ? state.trim().toUpperCase() : null)
                .region(region != null ? region.trim().toLowerCase() : null)
                .cep(cep != null ? cep.trim() : null)
                .build();
    }
}
