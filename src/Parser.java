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

        index = -1;
    }

    private String currentInstruction() {
        if (index < 0) {
            return "";
        }

        return instructions[index];
    }
    private boolean hasMoreLines() {
        return index < instructions.length;
    }

    private void advance() {
        if (hasMoreLines()) {
            index++;
        }
    }

    private void resetRead() {
        index = -1;
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
        String outname = extIndex == -1 ? filename + ".hack" : filename.substring(0, extIndex);

        PrintWriter writer;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(outname)));
            SymbolTable table = new SymbolTable();
            int lineNumber = 0;
            // first pass
            String sym;
            while (hasMoreLines()) {
                advance();
                switch (instructionType()) {
                    case C_INSTRUCTION, A_INSTRUCTION -> ++lineNumber;
                    case L_INSTRUCTION -> {
                        sym = symbol();
                        table.addEntry(sym, lineNumber + 1);
                    }
                }
            }

            lineNumber = 0;
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
                        writer.write("@"+table.getAddress(sym));
                    }
                    case C_INSTRUCTION -> {

                    }
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
