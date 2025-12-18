package br.com.joaonevesdev.fuelflow.api.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fuel_stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelStation {

    @Id
    @Column(name = "cnpj", length = 14)
    private String cnpj;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(length = 20)
    private String number;

    @Column(length = 100)
    private String complement;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 50)
    private String brand;

    @Column(name = "corporate_name", length = 200)
    private String corporateName;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FuelPrice> prices = new ArrayList<>();

    public static FuelStation fromCsvRecord(String cnpj, Address address,
                                            String number, String complement,
                                            String name, String brand) {
        return FuelStation.builder()
                .cnpj(cnpj)
                .address(address)
                .number(number != null ? number.trim() : null)
                .complement(complement != null ? complement.trim().toLowerCase() : null)
                .name(name != null ? name.trim().toLowerCase() : null)
                .brand(brand != null ? brand.trim().toLowerCase() : null)
                .build();
    }

    public static String cleanCnpj(String cnpj) {
        if (cnpj == null) return null;
        return cnpj.trim()
                .replace(".", "")
                .replace("/", "")
                .replace("-", "");
    }
}
