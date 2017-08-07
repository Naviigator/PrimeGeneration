import java.math.BigDecimal;

public class PrimeGeneratorTiming {
    private final long durationInNanoSeconds;
    private final BigDecimal beginSearch;
    private final BigDecimal endSearch;
    private final int numberOfPrimesFound;

    public PrimeGeneratorTiming(long durationInNanoSeconds, BigDecimal beginSearch, BigDecimal endSearch, int numberOfPrimesFound) {
        this.durationInNanoSeconds = durationInNanoSeconds;
        this.beginSearch = beginSearch;
        this.endSearch = endSearch;
        this.numberOfPrimesFound = numberOfPrimesFound;
    }

    public long getDurationInNanoSeconds() {
        return durationInNanoSeconds;
    }

    public long getDurationInMilliSeconds() {
        return durationInNanoSeconds / 1000000;
    }

    public BigDecimal getBeginSearch() {
        return beginSearch;
    }

    public BigDecimal getEndSearch() {
        return endSearch;
    }

    public int getNumberOfPrimesFound() {
        return numberOfPrimesFound;
    }
}
