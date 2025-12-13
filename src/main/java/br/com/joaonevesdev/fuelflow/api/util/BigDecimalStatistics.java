package br.com.joaonevesdev.fuelflow.api.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BigDecimalStatistics {

    public static class Summary {
        private final BigDecimal average;
        private final BigDecimal min;
        private final BigDecimal max;
        private final BigDecimal sum;
        private final long count;

        public Summary(BigDecimal average, BigDecimal min, BigDecimal max,
                       BigDecimal sum, long count) {
            this.average = average;
            this.min = min;
            this.max = max;
            this.sum = sum;
            this.count = count;
        }

        // Getters
        public BigDecimal getAverage() { return average; }
        public BigDecimal getMin() { return min; }
        public BigDecimal getMax() { return max; }
        public BigDecimal getSum() { return sum; }
        public long getCount() { return count; }
    }

    public Summary calculateStatistics(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            return new Summary(BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, 0);
        }

        BigDecimal sum = values.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal min = values.stream()
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal max = values.stream()
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal average = sum.divide(
                BigDecimal.valueOf(values.size()),
                2, RoundingMode.HALF_UP
        );

        return new Summary(average, min, max, sum, values.size());
    }

    public BigDecimal calculateMedian(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<BigDecimal> sorted = values.stream()
                .sorted()
                .collect(Collectors.toList());

        int size = sorted.size();
        if (size % 2 == 0) {
            BigDecimal mid1 = sorted.get(size / 2 - 1);
            BigDecimal mid2 = sorted.get(size / 2);
            return mid1.add(mid2)
                    .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        } else {
            return sorted.get(size / 2);
        }
    }
}