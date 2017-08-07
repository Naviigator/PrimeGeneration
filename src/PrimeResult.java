import java.math.BigDecimal;
import java.util.Set;

public class PrimeResult {
    private final BigDecimal maxGenerated;
    private final Set<BigDecimal> allPrimes;

    public PrimeResult(BigDecimal maxGenerated, Set<BigDecimal> allPrimes) {
        this.maxGenerated = maxGenerated;
        this.allPrimes = allPrimes;
    }

    public BigDecimal getMaxGenerated() {
        return maxGenerated;
    }

    public Set<BigDecimal> getAllPrimes() {
        return allPrimes;
    }
}
