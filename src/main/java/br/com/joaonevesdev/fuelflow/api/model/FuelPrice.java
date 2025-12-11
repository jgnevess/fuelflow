package br.com.joaonevesdev.fuelflow.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fuel_prices", indexes = {
        @Index(name = "idx_price_station_date", columnList = "station_cnpj, collectionDate"),
        @Index(name = "idx_price_date_product", columnList = "collectionDate, product")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(FuelPriceId.class)
public class FuelPrice {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_cnpj", nullable = false)
    @JsonIgnore
    private FuelStation station;

    @Id
    @Column(nullable = false)
    private LocalDate collectionDate;

    @Id
    @Column(nullable = false, length = 50)
    private String product;

    @Column(nullable = false, precision = 6, scale = 3)
    private BigDecimal salePrice;

    @Column(precision = 6, scale = 3)
    private BigDecimal purchasePrice;

    @Column(length = 20)
    private String measurementUnit;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public static BigDecimal parsePrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return null;
        }

        try {
            String normalized = priceStr.trim()
                    .replace("R$", "")
                    .replace("$", "")
                    .replace(" ", "")
                    .replace(".", "")
                    .replace(",", ".");

            return new BigDecimal(normalized);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de preço inválido: " + priceStr, e);
        }
    }

    public static FuelPrice fromCsvRecord(FuelStation station, LocalDate collectionDate,
                                          String product, String salePriceStr,
                                          String purchasePriceStr, String measurementUnit) {
        return FuelPrice.builder()
                .station(station)
                .collectionDate(collectionDate)
                .product(product != null ? product.trim() : null)
                .salePrice(parsePrice(salePriceStr))
                .purchasePrice(purchasePriceStr != null ? parsePrice(purchasePriceStr) : null)
                .measurementUnit(measurementUnit != null ? measurementUnit.trim() : null)
                .build();
    }
}