package br.com.joaonevesdev.fuelflow.api.model.dto;

import br.com.joaonevesdev.fuelflow.api.model.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationSummary {
    private BigDecimal averagePrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private List<BigDecimal> last3Prices;

    public BigDecimal getPriceVariation() {
        return maxPrice.subtract(minPrice);
    }

    public BigDecimal getStandardDeviation() {
        if (last3Prices == null || last3Prices.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal mean = last3Prices.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(last3Prices.size()), 6, RoundingMode.HALF_UP);

        BigDecimal variance = last3Prices.stream()
                .map(price -> price.subtract(mean).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(last3Prices.size()), 6, RoundingMode.HALF_UP);

        double stdDev = Math.sqrt(variance.doubleValue());
        return BigDecimal.valueOf(stdDev).setScale(2, RoundingMode.HALF_UP);
    }
}