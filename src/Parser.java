import java.io.*;

public class Parser {
    private String[] instructions;
    private int index;

    private final int VARIABLE_START = 16;
    private int VARIABLE_CURRENT = 0;
    private String filename;
    private boolean isComment(String src) {
        return src.startsWith("//");
    }

    private boolean isEmptyLine(String src) {
        return src.isBlank() || src.isEmpty();
    }

    public Parser(String filename) {
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader reader = new BufferedReader(fr);
            instructions = reader.lines().map(String::trim)
                    .filter(s -> isComment(s) || isEmptyLine(s))
                    .toArray(String[]::new);
            this.filename = filename;
        } catch (FileNotFoundException e) {
            System.err.println("No such file " + filename);
        }

        index = 0;
    }

    private String currentInstruction() {
        return instructions[index];
    }
    private boolean hasMoreLines() {
        return index < instructions.length;
    }

    private void advance() {
        index++;
    }

    private void resetRead() {
        index = 0;
    }
    private InstructionType instructionType() {
        final String instruction = currentInstruction();
        if (instruction.startsWith("@")) {
            return InstructionType.A_INSTRUCTION;
        } else if (instruction.startsWith("(") && instruction.endsWith(")")) {
            return InstructionType.L_INSTRUCTION;
        } else {
            return InstructionType.C_INSTRUCTION;
        }
    }

    private String symbol() {
        final String instruction = currentInstruction();
        final InstructionType type = instructionType();
        if (type == InstructionType.L_INSTRUCTION) {
            return instruction.substring(1, instruction.length() - 1);
        } else if (type == InstructionType.A_INSTRUCTION) {
            return instruction.substring(1);
        }

        return "";
    }

    private String dest() {
        final String instruction = currentInstruction();
        final int equalsIndex = instruction.indexOf("=");

        if (equalsIndex == -1) {
            return "";
        }

        return instruction.substring(0, instruction.indexOf("="));
    }

    private String comp() {
        final String instruction = currentInstruction();
        final InstructionType type = instructionType();

        final int equalsIndex = instruction.indexOf("=");
        final int jmpIndex = instruction.indexOf(";");

        final int startIndex = equalsIndex == -1 ? 0 : equalsIndex + 1;
        final int endIndex = jmpIndex == -1 ? instruction.length() : jmpIndex;

        if (type == InstructionType.C_INSTRUCTION) {
            return instruction.substring(startIndex, endIndex);
        }

        return "";
    }

    private String jump() {
        final String instruction = currentInstruction();
        final int jmpIndex = instruction.indexOf(";");

        if (jmpIndex == -1) {
            return "";
        }

        return instruction.substring(instruction.indexOf(";") + 1);
    }

    public void write() {
        int extIndex = filename.lastIndexOf(".");
        String outname = extIndex == -1 ? filename + ".hack" : filename.substring(0, extIndex) + ".hack";

        PrintWriter writer;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(outname)));
            SymbolTable table = new SymbolTable();
            int lineNumber = 0;
            // first pass
            String sym;
            while (hasMoreLines()) {
                switch (instructionType()) {
                    case C_INSTRUCTION, A_INSTRUCTION -> ++lineNumber;
                    case L_INSTRUCTION -> {
                        sym = symbol();
                        table.addEntry(sym, lineNumber + 1);
                    }
                }

                advance();
            }

            resetRead();

            // second pass
            while (hasMoreLines()) {
                advance();
                switch (instructionType()) {
                    case A_INSTRUCTION -> {
                        sym = symbol();
                        if (!table.contains(sym)) {
                            table.addEntry(sym, VARIABLE_START + VARIABLE_CURRENT);
                            ++VARIABLE_CURRENT;
                        }
                        int n = table.getAddress(sym);
                        String ns = Integer.toBinaryString(n);
                        int padLength = 15 - ns.length();
                        writer.write("0" + "0".repeat(padLength) + ns + "\n");
                    }
                    case C_INSTRUCTION -> {
                        String d = dest();
                        String c = comp();
                        String j = jump();

                        writer.write("111"+Code.aBit(c)+Code.comp(c)+Code.dest(d)+Code.jump(j) + "\n");
                    }
                }

                advance();
            }

            writer.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
