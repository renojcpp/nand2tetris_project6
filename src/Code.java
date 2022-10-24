import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Code {
    private static Map<String, String> compMnemonic = Map.ofEntries(
            Map.entry("0", "101010"),
            Map.entry("1", "111111"),
            Map.entry("-1", "111010"),
            Map.entry("D", "001100"),
            Map.entry("A", "110000"),
            Map.entry("M", "110000"),
            Map.entry("!D", "001101"),
            Map.entry("!A", "110001"),
            Map.entry("!M", "110001"),
            Map.entry("-D", "001111"),
            Map.entry("-A", "110011"),
            Map.entry("-M", "110011"),
            Map.entry("D+1", "011111"),
            Map.entry("A+1", "110111"),
            Map.entry("M+1", "110111"),
            Map.entry("D-1", "001110"),
            Map.entry("A-1", "110010"),
            Map.entry("M-1", "110010"),
            Map.entry("D+A", "000010"),
            Map.entry("D+M", "000010"),
            Map.entry("D-A", "010011"),
            Map.entry("D-M", "010011"),
            Map.entry("A-D", "000111"),
            Map.entry("M-D", "000111"),
            Map.entry("D&A", "000000"),
            Map.entry("D&M", "000000"),
            Map.entry("D|A", "010101"),
            Map.entry("D|M", "010101")
    );

    private static Map<String, String> destMnemonic = Map.ofEntries(
            Map.entry("", "000"),
            Map.entry("M", "001"),
            Map.entry("D", "010"),
            Map.entry("DM", "011"),
            Map.entry("A", "100"),
            Map.entry("AM", "101"),
            Map.entry("AD", "110"),
            Map.entry("ADM", "111")
    );

    private static Map<String, String> jumpMnemonic = Map.ofEntries(
            Map.entry("", "000"),
            Map.entry("JGT", "001"),
            Map.entry("JEQ", "010"),
            Map.entry("JGE", "011"),
            Map.entry("JLT", "100"),
            Map.entry("JNE", "101"),
            Map.entry("JLE", "110"),
            Map.entry("JMP", "111")
    );

    private static Set<String> aBits = Set.of("M", "!M", "-M", "M+1", "M-1", "D+M", "D-M", "M-D", "D&M", "D|M");

    public static String aBit(String mnemonic) {
        if (aBits.contains(mnemonic)) {
            return "1";
        }

        return "0";
    }
    public static String dest(String mnemonic) {
        return destMnemonic.get(mnemonic);
    }

    public static String comp(String mnemonic) {
        return compMnemonic.get(mnemonic);
    }

    public static String jump(String mnemonic) {
        return jumpMnemonic.get(mnemonic);
    }
}
