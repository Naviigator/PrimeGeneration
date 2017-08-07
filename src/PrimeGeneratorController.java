import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class PrimeGeneratorController {
    private final int cores;

    private BigDecimal maxCalculationsPerCore;
    private BigDecimal minCalculationsPerCore;
    private PrimeResult calculatedPrimes;

    private volatile boolean running = false;

    private final AtomicReferenceArray<PrimeGenerator> generatorsCurrentBatch;
    private final Collection<PrimeGeneratorTiming> timings = new ArrayList<>();
    private CountDownLatch latch = null;
    private CountDownLatch stopLatch = null;

    public PrimeGeneratorController(PrimeResult calculatedPrimes) {
        this.cores = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        System.out.println(this.cores + " thread" + (this.cores == 1 ? "" : "s") + " will be used.");
        PrimeGenerator[] generators = new PrimeGenerator[cores];
        for (int i = 0; i < cores; ++i) {
            generators[i] = null;
        }
        generatorsCurrentBatch = new AtomicReferenceArray<>(generators);

        this.calculatedPrimes = calculatedPrimes;
        this.maxCalculationsPerCore = new BigDecimal(20000);
        this.minCalculationsPerCore = new BigDecimal(5000);
    }

    public void startGenerating() {
        final ExecutorService executorService = Executors.newFixedThreadPool(this.cores);
        running = true;
        while (running) {
            latch = new CountDownLatch(1);
            Set<BigDecimal> previouslyGeneratedPrimes = calculatedPrimes.getAllPrimes();
            BigDecimal lowestPotentialPrime = calculatedPrimes
                    .getMaxGenerated()
                    .add(new BigDecimal(2));
            BigDecimal maxCalculatablePrime = BigDecimalMath
                    .min(calculatedPrimes
                                    .getMaxGenerated()
                                    .pow(2),
                            lowestPotentialPrime
                                    .add(maxCalculationsPerCore
                                            .multiply(new BigDecimal(cores))));

            int coresToUse;
            BigDecimal sizePerCore = null;
            for (coresToUse = cores; coresToUse > 0; --coresToUse) {
                sizePerCore = maxCalculatablePrime
                        .subtract(lowestPotentialPrime)
                        .divide(new BigDecimal(coresToUse), BigDecimal.ROUND_FLOOR);
                if (sizePerCore.compareTo(minCalculationsPerCore) > 0) {
                    break;
                }
            }
            if (coresToUse < 1) {
                coresToUse = 1;
            }
            //noinspection ConstantConditions
            if (sizePerCore.remainder(new BigDecimal(2)).equals(BigDecimal.ONE)) {
                sizePerCore = sizePerCore.subtract(BigDecimal.ONE);
            }

            BigDecimal lowest = lowestPotentialPrime;
            BigDecimal highest = lowest.add(sizePerCore);
            Collection<PrimeGenerator> generators = new ArrayList<>();
            for (int i = 0; i < coresToUse; ++i) {
                if (i != 0) {
                    lowest = lowest.add(new BigDecimal(2));
                    highest = lowest.add(sizePerCore);
                }
                PrimeGenerator generator = new PrimeGenerator(lowest, highest, previouslyGeneratedPrimes);
                generatorsCurrentBatch.set(i, generator);
                generators.add(generator);
            }
            try {
                latch = new CountDownLatch(generators.size());
                for (final PrimeGenerator runningGenerator : generators) {
                    executorService.submit(() -> {
                        runningGenerator.generate();
                        generatorDone(runningGenerator);
                    });
                }
                latch.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException("latch await error");
            }
            Set<BigDecimal> newPrimes = new TreeSet<>(calculatedPrimes.getAllPrimes());
            for (PrimeGenerator currentBatch : generators) {
                newPrimes.addAll(currentBatch.getPrimes());
                timings.add(currentBatch.getTimings());
            }
            calculatedPrimes = new PrimeResult(maxCalculatablePrime, newPrimes);
        }
        executorService.shutdown();
        stopLatch.countDown();
    }

    private void generatorDone(PrimeGenerator generator) {
        if (!GeneratorState.DONE.equals(generator.getGeneratorState())) {
            throw new IllegalStateException("generator not done, but generatorDone called.");
        }
        latch.countDown();
    }

    public void stopGenerating(Runnable runnable) {
        if (!running) {
            throw new IllegalStateException("stopping when not running...");
        }
        running = false;
        try {
            stopLatch = new CountDownLatch(1);
            stopLatch.await();
        } catch (Exception e) {
            throw new IllegalStateException("something went wrong while stopping...");
        }
        runnable.run();
    }

    public PrimeResult getResult() {
        return calculatedPrimes;
    }

    public BigDecimal getMinCalculationsPerCore() {
        return minCalculationsPerCore;
    }

    public BigDecimal getMaxCalculationsPerCore() {
        return maxCalculationsPerCore;
    }

    public void setMinCalculationsPerCore(BigDecimal minCalculationsPerCore) {
        this.minCalculationsPerCore = minCalculationsPerCore;
    }

    public void setMaxCalculationsPerCore(BigDecimal maxCalculationsPerCore) {
        this.maxCalculationsPerCore = maxCalculationsPerCore;
    }

    public Collection<PrimeGeneratorTiming> getTimings() {
        return timings;
    }
}
