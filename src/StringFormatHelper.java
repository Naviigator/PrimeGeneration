import java.math.BigDecimal;
import java.util.Stack;

public class StringFormatHelper {
    private static final String THOUSANDS_SEPARATOR = ".";

    public static String formatNumber(long number) {
        Stack<String> parts = new Stack<>();
        while (number > 0) {
            long remainder = number % 1000;
            number = number / 1000;
            parts.push(String.valueOf(remainder));
        }
        return stackToString(parts);
    }

    public static String formatNumber(BigDecimal number) {
        Stack<String> parts = new Stack<>();
        String numberAsString = number.toString();
        for (int i = numberAsString.length(); i >= 0; i -= 3) {
            parts.push(String.valueOf(numberAsString.substring(Math.max(0, i - 3), i)));
        }
        return stackToString(parts);
    }

    private static String stackToString(Stack<String> parts) {
        StringBuilder builder = new StringBuilder();
        while (!parts.empty()) {
            String part = parts.pop();
            String buffer = "";
            if (builder.length() > 0) {
                builder.append(THOUSANDS_SEPARATOR);
                if (part.length() == 1) {
                    buffer = "00";
                }
                if (part.length() == 2) {
                    buffer = "0";
                }
            }
            builder.append(buffer);
            builder.append(part);
        }
        String result = builder.toString();
        while (result.startsWith("0")) {
            result = result.substring(1);
        }
        return result;
    }
}
