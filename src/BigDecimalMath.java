import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.BigDecimal.*;

/**
 * Partial class from https://github.com/eobermuhlner/big-math
 */
public class BigDecimalMath {

    private static final BigDecimal TWO = valueOf(2);

    private static final BigDecimal DOUBLE_MAX_VALUE = BigDecimal.valueOf(Double.MAX_VALUE);
    private static final int EXPECTED_INITIAL_PRECISION = 17;
    private static BigDecimal[] factorialCache = new BigDecimal[100];

    static {
        BigDecimal result = ONE;
        factorialCache[0] = result;
        for (int i = 1; i < factorialCache.length; i++) {
            result = result.multiply(valueOf(i));
            factorialCache[i] = result;
        }
    }

    private BigDecimalMath() {
        // prevent instances
    }

    /**
     * Returns whether the specified {@link BigDecimal} value can be represented as <code>double</code>.
     * <p>
     * <p>If this returns <code>true</code> you can call {@link BigDecimal#doubleValue()}
     * without fear of getting {@link Double#POSITIVE_INFINITY} or {@link Double#NEGATIVE_INFINITY} as calculatedPrimes.</p>
     * <p>
     * <p>Example: <code>BigDecimalMath.isDoubleValue(new BigDecimal("1E309"))</code> returns <code>false</code>,
     * because <code>new BigDecimal("1E309").doubleValue()</code> returns <code>Infinity</code>.</p>
     * <p>
     * <p>Note: This method does <strong>not</strong> check for possible loss of precision.</p>
     * <p>
     * <p>For example <code>BigDecimalMath.isDoubleValue(new BigDecimal("1.23400000000000000000000000000000001"))</code> will return <code>true</code>,
     * because <code>new BigDecimal("1.23400000000000000000000000000000001").doubleValue()</code> returns a valid double value,
     * although it loses precision and returns <code>1.234</code>.</p>
     * <p>
     * <p><code>BigDecimalMath.isDoubleValue(new BigDecimal("1E-325"))</code> will return <code>true</code>
     * although this value is smaller than {@link Double#MIN_VALUE} (and therefore outside the range of values that can be represented as <code>double</code>)
     * because <code>new BigDecimal("1E-325").doubleValue()</code> returns <code>0</code> which is a legal value with loss of precision.</p>
     *
     * @param value the {@link BigDecimal} to check
     * @return <code>true</code> if the value can be represented as <code>double</code> value
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isDoubleValue(BigDecimal value) {
        if (value.compareTo(DOUBLE_MAX_VALUE) > 0) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (value.compareTo(DOUBLE_MAX_VALUE.negate()) < 0) {
            return false;
        }

        return true;
    }

    /**
     * Calculates the square root of {@link BigDecimal} x.
     * <p>
     * <p>See <a href="http://en.wikipedia.org/wiki/Square_root">Wikipedia: Square root</a></p>
     *
     * @param x           the {@link BigDecimal} value to calculate the square root
     * @param mathContext the {@link MathContext} used for the calculatedPrimes
     * @return the calculated square root of x with the precision specified in the <code>mathContext</code>
     * @throws ArithmeticException if x &lt; 0
     */
    public static BigDecimal sqrt(BigDecimal x, MathContext mathContext) {
        switch (x.signum()) {
            case 0:
                return ZERO;
            case -1:
                throw new ArithmeticException("Illegal sqrt(x) for x < 0: x = " + x);
        }

        int maxPrecision = mathContext.getPrecision() + 4;
        BigDecimal acceptableError = ONE.movePointLeft(mathContext.getPrecision() + 1);

        BigDecimal result;
        if (isDoubleValue(x)) {
            result = BigDecimal.valueOf(Math.sqrt(x.doubleValue()));
        } else {
            result = x.divide(TWO, mathContext);
        }

        if (result.multiply(result, mathContext).compareTo(x) == 0) {
            return result.round(mathContext); // early exit if x is a square number
        }

        int adaptivePrecision = EXPECTED_INITIAL_PRECISION;
        BigDecimal last;

        do {
            last = result;
            adaptivePrecision = adaptivePrecision * 2;
            if (adaptivePrecision > maxPrecision) {
                adaptivePrecision = maxPrecision;
            }
            MathContext mc = new MathContext(adaptivePrecision, mathContext.getRoundingMode());
            result = x.divide(result, mc).add(last, mc).divide(TWO, mc);
        } while (adaptivePrecision < maxPrecision || result.subtract(last).abs().compareTo(acceptableError) > 0);

        return result.round(mathContext);
    }

    public static BigDecimal min(BigDecimal bd1, BigDecimal bd2) {
        return bd1.compareTo(bd2) > 0 ? bd2 : bd1;
    }
}