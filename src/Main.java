import java.util.concurrent.Executors;

public class Main {
    private static PrimeGeneratorController controller;

    public static void main(String[] args) {
        PrimeResult primeResult = PrimeStorage.get();
        controller = new PrimeGeneratorController(primeResult);
        Executors.newSingleThreadExecutor().submit(() -> controller.startGenerating());
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        controller.stopGenerating(Main::save);
    }

    private static void save() {
        System.out.println("all done.");
        if (controller == null) {
            throw new IllegalStateException("can't save without generating first.");
        }
        PrimeStorage.save(controller.getResult());
        System.out.println("timings:");
        for (PrimeGeneratorTiming primeGeneratorTiming : controller.getTimings()) {
            System.out.print("begin: ");
            System.out.print(StringFormatHelper
                    .formatNumber(primeGeneratorTiming
                            .getBeginSearch()));
            System.out.print(", end: ");
            System.out.print(StringFormatHelper
                    .formatNumber(primeGeneratorTiming
                            .getEndSearch()));
            System.out.print(", nr. of primes: ");
            System.out.print(StringFormatHelper
                    .formatNumber(primeGeneratorTiming
                            .getNumberOfPrimesFound()));
            System.out.print(", calculated in milliseconds: ");
            System.out.println(StringFormatHelper
                    .formatNumber(primeGeneratorTiming
                            .getDurationInMilliSeconds()));
        }
    }
}
