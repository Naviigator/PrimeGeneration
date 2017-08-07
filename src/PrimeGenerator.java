import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Set;
import java.util.TreeSet;

public class PrimeGenerator {
    private BigDecimal from;
    private BigDecimal current;
    private BigDecimal to;
    volatile private GeneratorState state = GeneratorState.READY;

    private Set<BigDecimal> primes = new TreeSet<>();
    private Set<BigDecimal> oldPrimes;
    private long startTime;
    private long endTime;

    private final BigDecimal TO_ADD = new BigDecimal(2);

    public PrimeGenerator(BigDecimal from, BigDecimal to, Set<BigDecimal> oldPrimes) {
        if (from.remainder(TO_ADD).equals(new BigDecimal(0))) {
            from = from.add(BigDecimal.ONE);
        }
        this.from = from;
        this.current = from;
        this.to = to;
        this.oldPrimes = oldPrimes;
    }

    public void generate() {
        state = GeneratorState.WORKING;
        startTime = System.nanoTime();
        while (current.compareTo(this.to) <= 0) {
            BigDecimal nextPotentialPrimeSqrt = BigDecimalMath
                    .sqrt(current, MathContext.UNLIMITED);
            boolean isPrime = true;//everything has a chance to be a prime!
            for (BigDecimal prime : oldPrimes) {
                if (prime.compareTo(nextPotentialPrimeSqrt) > 0) {
                    break;
                } else if (current
                        .remainder(prime)
                        .compareTo(BigDecimal.ZERO) == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                primes.add(current);
            }
            current = current.add(TO_ADD);
        }
        endTime = System.nanoTime();
        state = GeneratorState.DONE;
    }

    public GeneratorState getGeneratorState() {
        return state;
    }

    public Set<BigDecimal> getPrimes() {
        if (!GeneratorState.DONE.equals(getGeneratorState())) {
            throw new IllegalStateException("not done generating");
        }
        return primes;
    }

    public PrimeGeneratorTiming getTimings() {
        if (!GeneratorState.DONE.equals(getGeneratorState())) {
            throw new IllegalStateException("not done generating");
        }
        return new PrimeGeneratorTiming(endTime - startTime, from, to, primes.size());
    }
}
