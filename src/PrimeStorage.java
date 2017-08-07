import java.io.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

public class PrimeStorage {
    private static String DIRECTORY_NAME = "./primes";
    private static String FILE_EXTENSION = ".prime";

    public static PrimeResult get() {
        TreeSet<BigDecimal> result = new TreeSet<>();
        BigDecimal highestNumber = new BigDecimal(3);
        try {
            File primesDirectory = new File(DIRECTORY_NAME);
            if (!primesDirectory.exists()) {
                //noinspection ResultOfMethodCallIgnored
                primesDirectory.mkdir();
            }
            File[] files = primesDirectory.listFiles(file -> file.getName().endsWith(FILE_EXTENSION));
            //noinspection ConstantConditions
            for (File file : files) {
                String fileName = file.getName();
                BigDecimal filenameAsBigDecimal = new BigDecimal(
                        fileName.substring(0, fileName.length() - FILE_EXTENSION.length()));
                if (highestNumber.compareTo(filenameAsBigDecimal) < 0) {
                    highestNumber = filenameAsBigDecimal;
                }
            }
            BufferedReader br = new BufferedReader(
                    new FileReader(DIRECTORY_NAME + "/" + highestNumber.toString() + FILE_EXTENSION));
            String line;
            while ((line = br.readLine()) != null) {
                result.add(new BigDecimal(line));
            }
        } catch (Exception e) {
            result.add(new BigDecimal(3));
        }
        if (result.contains(new BigDecimal(2))) {
            result.remove(new BigDecimal(2));
        }
        if (!result.contains(new BigDecimal(3))) {
            result.remove(new BigDecimal(3));
        }
        return new PrimeResult(highestNumber, result);
    }

    public static void save(PrimeResult primeResult) {
        Set<BigDecimal> primes = primeResult.getAllPrimes();
        if (!primes.contains(new BigDecimal(2))) {
            primes.add(new BigDecimal(2));
        }
        String fileName = DIRECTORY_NAME + "/" + primeResult.getMaxGenerated() + FILE_EXTENSION;
        try {
            File file = new File(fileName);
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(fileName));
            boolean written = false;
            for (BigDecimal prime : primes) {
                if (written) {
                    bw.newLine();
                }
                bw.write(prime.toString());
                written = true;
            }
            bw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
