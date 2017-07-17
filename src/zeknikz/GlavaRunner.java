package zeknikz;

import de.hunsicker.io.FileFormat;
import de.hunsicker.jalopy.Jalopy;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

class GlavaRunner {
    private String code;
    private boolean debug;
    private String[] args;
    private ArrayList<String> openSeparators = new ArrayList<>();
    private boolean inString;
    private boolean inLineComment;
    private boolean inBlockComment;
    private GlavaCLArg[] CLArgs;

    class GlavaCLArg {
        String type;
        String value;

        GlavaCLArg(String value) {
            if (value.matches("^-?\\d+$")) {
                this.type = "int";
                this.value = value;
            } else if (value.matches("^-?(\\d+\\.?\\d*f|\\d*\\.?\\d+f)$")) {
                this.type = "float";
                this.value = value;
            } else if (value.matches("^-?(\\d+\\.?\\d*|\\d*\\.?\\d+)$")) {
                this.type = "double";
                this.value = value;
            } else if (value.matches("^'.'?$")) {
                this.type = "char";
                this.value = value.substring(0, 2);
            } else if (value.matches("^true|false$")) {
                this.type = "boolean";
                this.value = value;
            } else {
                this.type = "String";
                this.value = "\"" + StringEscapeUtils.escapeJava(value) + "\"";
                this.value = StringEscapeUtils.escapeJava(this.value);
            }
        }
    }

    GlavaRunner(String code, boolean debug, String[] args) {
        this.code = code;
        this.debug = debug;
        this.args = args;
        Map<String, String> typeArgs = new HashMap<>();
        if (args != null) {
            CLArgs = new GlavaCLArg[args.length];
            for (int i = 0; i < args.length; i++) {
                CLArgs[i] = new GlavaCLArg(args[i]);
                typeArgs.putIfAbsent(CLArgs[i].type, "");
                typeArgs.put(CLArgs[i].type, typeArgs.get(CLArgs[i].type) + (CLArgs[i].type.equals("String") ? StringEscapeUtils.unescapeJava(CLArgs[i].value) : CLArgs[i].value) + (CLArgs[i].type.equals("char") ? "'" : "") + ", ");
            }
        }
        StringBuilder typeArgStr = new StringBuilder();
        for (String type : typeArgs.keySet()) {
            typeArgStr.append(String.format("%s[] glava_args_%s = new %s[]{%s};\n", type, type, type, typeArgs.get(type)));
        }
        GLAVA_ARGS = typeArgStr.toString();
    }

    @FunctionalInterface
    private interface ReplacementConsumer {
        String getReplacement();
    }

    private class Replacement {
        private String regex;
        private int checkCharsBefore;
        private int checkCharsAfter;
        private ReplacementConsumer replacement;

        private Replacement(String regex, int checkCharsBefore, int checkCharsAfter, String replacement) {
            this(regex, checkCharsBefore, checkCharsAfter, () -> replacement);
        }

        private Replacement(String regex, int checkCharsBefore, int checkCharsAfter, ReplacementConsumer replacement) {
            this.regex = regex;
            this.checkCharsBefore = checkCharsBefore;
            this.checkCharsAfter = checkCharsAfter;
            this.replacement = replacement;
        }
    }

    private static final String COMMON_IMPORTS = "import java.util.*;\nimport static java.lang.Math.*;\nimport java.io.*;\nimport java.awt.*;\nimport javax.swing.*;\nimport java.text.*;\nimport java.util.regex.*;\n";
    private static final String COMMON_VARIABLES = "public static int m = 0;\npublic static int n = 1;\npublic static double d = 0d;\npublic static float f = 0f;\npublic static String s = \"\";\npublic static String t = \"\";\npublic static String u = \"abcdefghijklmnopqrstuvwxyz\";\npublic static String U = \"ABCDEFGHIJKLMNOPQRSTUVWXYZ\";\npublic static char c = 'A';\npublic static boolean T = true;\npublic static boolean F = false;\n";
    private static String GLAVA_ARGS;

    private static final String KEY_CHARACTERS = "βχδφισΠΣΔΦΓηεΞωΩπλκυύϋζ⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽①②③④⑤⑥⑦⑧⑨⑩ⒷⒸⒹⒻⒾⓈ";
    private static final String GROUP_OPENING_CHARACTERS = "({[";
    private static final String GROUP_CLOSING_CHARACTERS = ")}]";

    private Replacement[] replacements = {
            // Native Types (and String)
            new Replacement("(?<![a-zA-Z])β", 1, 0, " boolean "),
            new Replacement("(?<![a-zA-Z])χ", 1, 0, " char "),
            new Replacement("(?<![a-zA-Z])δ", 1, 0, " double "),
            new Replacement("(?<![a-zA-Z])φ", 1, 0, " float "),
            new Replacement("(?<![a-zA-Z])ι", 1, 0, " int "),
            new Replacement("(?<![a-zA-Z])σ", 1, 0, " String "),

            // Native Type Objects
            new Replacement("(?<![a-zA-Zη])Π", 1, 0, " Boolean "),
            new Replacement("(?<![a-zA-Zη])Σ", 1, 0, " Character "),
            new Replacement("(?<![a-zA-Zη])Δ", 1, 0, " Double "),
            new Replacement("(?<![a-zA-Zη])Φ", 1, 0, " Float "),
            new Replacement("(?<![a-zA-Zη])Γ", 1, 0, " Integer "),

            // Native Type Objects w/ new Keyword
            new Replacement("(?<![a-zA-Z])ηΠ", 1, 1, () -> {
                openSeparators.add(")");
                return " new Boolean( ";
            }),
            new Replacement("(?<![a-zA-Z])ηΣ", 1, 1, () -> {
                openSeparators.add(")");
                return " new Character( ";
            }),
            new Replacement("(?<![a-zA-Z])ηΔ", 1, 1, () -> {
                openSeparators.add(")");
                return " new Double( ";
            }),
            new Replacement("(?<![a-zA-Z])ηΦ", 1, 1, () -> {
                openSeparators.add(")");
                return " new Float( ";
            }),
            new Replacement("(?<![a-zA-Z])ηΓ", 1, 1, () -> {
                openSeparators.add(")");
                return " new Integer( ";
            }),

            // Casting
            new Replacement("(?<![a-zA-Z])ζΠ", 1, 1, " (boolean) "),
            new Replacement("(?<![a-zA-Z])ζΣ", 1, 1, " (char) "),
            new Replacement("(?<![a-zA-Z])ζΔ", 1, 1, " (double) "),
            new Replacement("(?<![a-zA-Z])ζΦ", 1, 1, " (float) "),
            new Replacement("(?<![a-zA-Z])ζΓ", 1, 1, " (int) "),
            new Replacement("(?<![a-zA-Z])ζσ", 1, 1, " (String) "),

            // Loops
            new Replacement("(?<![a-zA-Z])ε", 1, 0, () -> {
                openSeparators.add("){");
                debugLog("Opened group of type WHILE_LOOP_BASIC.");
                return " while( ";
            }),
            new Replacement("(?<![a-zA-Z])Ξ", 1, 0, () -> {
                openSeparators.add("{");
                debugLog("Opened group of type WHILE_LOOP_INFINITE.");
                return " while(true){ ";
            }),
            new Replacement("(?<![a-zA-Z])ω", 1, 0, () -> {
                openSeparators.add("){");
                debugLog("Opened group of type FOR_LOOP_BASIC.");
                return " for( ";
            }),
            new Replacement("(?<![a-zA-Z])Ω", 1, 0, () -> {
                openSeparators.add(";}");
                debugLog("Opened two groups of type FOR_LOOP_DEFAULT");
                openSeparators.add(";i++){");
                return " for(int i=0;i< ";
            }),

            // Printing
            new Replacement("(?<![a-zA-Z])π", 1, 0, () -> {
                openSeparators.add(")");
                return " System.out.print( ";
            }),
            new Replacement("(?<![a-zA-Z])λ", 1, 0, () -> {
                openSeparators.add(")");
                return " System.out.println( ";
            }),
            new Replacement("(?<![a-zA-Z])κ", 1, 0, () -> {
                openSeparators.add(")");
                return " System.out.printf( ";
            }),

            // Iterable Size
            new Replacement("υ", 0, 0, " .length() "),
            new Replacement("ύ", 0, 0, " .length "),
            new Replacement("ϋ", 0, 0, " .size() "),

            // Arguments
            new Replacement("(?<![a-zA-Z])⑴", 1, 0, "A[0]"),
            new Replacement("(?<![a-zA-Z])⑵", 1, 0, "A[1]"),
            new Replacement("(?<![a-zA-Z])⑶", 1, 0, "A[2]"),
            new Replacement("(?<![a-zA-Z])⑷", 1, 0, "A[3]"),
            new Replacement("(?<![a-zA-Z])⑸", 1, 0, "A[4]"),
            new Replacement("(?<![a-zA-Z])⑹", 1, 0, "A[5]"),
            new Replacement("(?<![a-zA-Z])⑺", 1, 0, "A[6]"),
            new Replacement("(?<![a-zA-Z])⑻", 1, 0, "A[7]"),
            new Replacement("(?<![a-zA-Z])⑼", 1, 0, "A[8]"),
            new Replacement("(?<![a-zA-Z])⑽", 1, 0, "A[9]"),

            // Converted Arguments
            new Replacement("(?<![a-zA-Z])①", 1, 0, () -> getArgValue(0)),
            new Replacement("(?<![a-zA-Z])②", 1, 0, () -> getArgValue(1)),
            new Replacement("(?<![a-zA-Z])③", 1, 0, () -> getArgValue(2)),
            new Replacement("(?<![a-zA-Z])④", 1, 0, () -> getArgValue(3)),
            new Replacement("(?<![a-zA-Z])⑤", 1, 0, () -> getArgValue(4)),
            new Replacement("(?<![a-zA-Z])⑥", 1, 0, () -> getArgValue(5)),
            new Replacement("(?<![a-zA-Z])⑦", 1, 0, () -> getArgValue(6)),
            new Replacement("(?<![a-zA-Z])⑧", 1, 0, () -> getArgValue(7)),
            new Replacement("(?<![a-zA-Z])⑨", 1, 0, () -> getArgValue(8)),
            new Replacement("(?<![a-zA-Z])⑩", 1, 0, () -> getArgValue(9)),

            // Typed Arguments
            new Replacement("(?<![a-zA-Z])Ⓑ", 1, 0, () -> {
                openSeparators.add("]");
                return " glava_args_boolean[ ";
            }),
            new Replacement("(?<![a-zA-Z])Ⓒ", 1, 0, () -> {
                openSeparators.add("]");
                return " glava_args_char[ ";
            }),
            new Replacement("(?<![a-zA-Z])Ⓓ", 1, 0, () -> {
                openSeparators.add("]");
                return " glava_args_double[ ";
            }),
            new Replacement("(?<![a-zA-Z])Ⓕ", 1, 0, () -> {
                openSeparators.add("]");
                return " glava_args_float[ ";
            }),
            new Replacement("(?<![a-zA-Z])Ⓘ", 1, 0, () -> {
                openSeparators.add("]");
                return " glava_args_int[ ";
            }),
            new Replacement("(?<![a-zA-Z])Ⓢ", 1, 0, () -> {
                openSeparators.add("]");
                return " glava_args_String[ ";
            }),

            // Misc
            new Replacement("(?<![a-zA-Z])η(?![ΠΣΔΦΓ])", 1, 1, " null ")
    };


    private String getArgValue(int argNum) {
        return " " + CLArgs[argNum].value + " ";
    }

    @SuppressWarnings({"unchecked", "StringConcatenationInLoop"})
    void convertToJavaCode() throws GlavaError {
        debugLog("Converting code...");
        mainLoop:
        for (int index = 0; index < this.code.length(); ) {
            // Line Comments
            if (this.code.charAt(index) == '\\') {
                if (index + 1 < this.code.length() && this.code.charAt(index + 1) == '\\') {
                    debugLog("Line Comment start detected at index " + index + ".");
                    inLineComment = true;
                } else {
                    index++;
                }
            } else if (inLineComment) {
                if (this.code.charAt(index) == '\n') {
                    debugLog("Line Comment ended at index " + index + ".");
                    inLineComment = false;
                } else {
                    index++;
                }
            }
            // Block Comments
            else if (this.code.charAt(index) == '/') {
                if (index + 1 < this.code.length() && this.code.charAt(index + 1) == '*') {
                    debugLog("Block Comment start detected at index " + index + ".");
                    inBlockComment = true;
                } else {
                    index++;
                }
            } else if (inBlockComment) {
                if (this.code.charAt(index) == '*' && index + 1 < this.code.length() && this.code.charAt(index + 1) == '/') {
                    debugLog("Block Comment ended at index " + index + ".");
                    inBlockComment = false;
                } else {
                    index++;
                }
            }
            // Strings
            else if (this.code.charAt(index) == '"') {
                if (index > 0 && this.code.charAt(index - 1) == '\\') {
                    index++;
                } else if (!inString) {
                    debugLog("String start detected at index " + index + ".");
                    inString = true;
                    openSeparators.add("\"");
                    index++;
                } else {
                    debugLog("String ended at index " + index + ".");
                    inString = false;
                    openSeparators.remove(openSeparators.size() - 1);
                    index++;
                }
            } else if (inString) {
                if (this.code.charAt(index) == '\n') {
                    this.code = this.code.replaceFirst(String.format("(?<=^.{%s})\n", index), "\\n");
                    debugLog("Linefeed detected in string at index " + index + ".", true);
                    index += 2;
                } else {
                    index++;
                }
            }
            // Character Literal
            else if (this.code.charAt(index) == '\'') {
                if (index + 2 < this.code.length()) {
                    if (this.code.charAt(index + 1) == '\\') {
                        this.code = insertStringAtPoint(this.code, index + 2, "\\'", 0);
                        debugLog("Character literal detected at index " + index + ".", true);
                        index += 4;
                    } else {
                        this.code = insertStringAtPoint(this.code, index + 2, "'", 0);
                        debugLog("Character literal detected at index " + index + ".", true);
                        index += 3;
                    }
                } else {
                    throw new GlavaError("Index out of range when scanning for char literal.");
                }
            } else if (KEY_CHARACTERS.contains("" + this.code.charAt(index))) {
                for (Replacement rp : replacements) {
                    if (Pattern.compile(rp.regex).matcher(this.code.substring(Math.max(0, index - rp.checkCharsBefore), Math.min(index + rp.checkCharsAfter + 1, this.code.length()))).find()) {
                        String rpStr = rp.replacement.getReplacement();
                        char rpChar = this.code.charAt(index);
                        this.code = this.code.replaceFirst(String.format("(?<=^.{%s})%s", index, rp.regex), rpStr);
                        debugLog(String.format("Replaced \"%s\" with \"%s\" at index %d.", "" + rpChar, rpStr, index), true);
                        index += rpStr.length();
                        continue mainLoop;
                    }
                }
                index++;
            } else if (GROUP_OPENING_CHARACTERS.contains("" + this.code.charAt(index))) {
                switch (this.code.charAt(index)) {
                    case '(':
                        openSeparators.add(")");
                        break;
                    case '{':
                        openSeparators.add(";}");
                        break;
                    case '[':
                        openSeparators.add("]");
                        break;
                }
                debugLog("Opened group of type '" + this.code.charAt(index) + "' at index " + index + ".");
                index++;
            } else if (GROUP_CLOSING_CHARACTERS.contains("" + this.code.charAt(index))) {
                if (openSeparators.size() == 0) {
                    index++;
                    continue;
                }
                String closer = openSeparators.remove(openSeparators.size() - 1);
                this.code = insertStringAtPoint(this.code, index, closer, 1);
                debugLog("Closed group with '" + closer + "' at index " + index + ".", true);
                index += closer.length();
            } else {
                index++;
            }
        }
        if (openSeparators.size() > 0) {
            for (int i = openSeparators.size() - 1; i >= 0; i--) {
                String closer = openSeparators.remove(i);
                this.code += closer;
                debugLog("Closed group with '" + closer + "' at index " + this.code.length() + ".", true);
            }
        }

        boolean mainMethod = false;
        boolean mainClass = false;
        if (this.code.contains("public static void main")) {
            mainMethod = true;
        }
        if (Pattern.compile("public class (.|\\n)*\\{(.|\\n)*public static void main").matcher(this.code).find()) {
            mainClass = true;
        }

        if (!mainClass && !mainMethod) {
            this.code = COMMON_IMPORTS + "public class Main {" + GLAVA_ARGS + "\n" + COMMON_VARIABLES + "public static void main(String[] A) {" + this.code + ";}}";
            debugLog("Surrounded code with default class and main method.");
        } else if (!mainClass) {
            this.code = COMMON_IMPORTS + "public class Main {" + GLAVA_ARGS + "\n" + COMMON_VARIABLES + this.code + "}";
            debugLog("Surrounded code with default class.");
        }
        if (debug) {

            debugLog("Complete code:\n" + formatJavaCode(this.code));
        }
        debugLog("Code conversion complete.");
        if (debug) System.out.println();
    }

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    void runCode() throws Exception {
        debugLog("Compiling...");
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager sjfm = jc.getStandardFileManager(null, null, null);
        File jf = new File("Main.java"); //create file in current working directory
        PrintWriter pw = new PrintWriter(jf);
        pw.println(this.getCode());
        pw.close();
        Iterable fO = sjfm.getJavaFileObjects(jf);
        if (!jc.getTask(null, sjfm, null, null, null, fO).call()) { //compile the code
            throw new GlavaError("Compilation failed.");
        }
        debugLog("Code compiled successfully.");

        debugLog("Running code...");
        URL[] urls = new URL[]{new File("").toURI().toURL()}; //use current working directory
        URLClassLoader ucl = new URLClassLoader(urls);
        Object o = ucl.loadClass("Main").newInstance();
        o.getClass().getMethod("main", String[].class).invoke(o, new Object[]{this.args});

        debugLog("Cleaning up...");
        jf.delete();
        new File("Main.class").delete();
        debugLog("Code execution complete.");
    }

    private static String insertStringAtPoint(String str, int index, String toInsert, int removeAmount) {
        return str.substring(0, index) + toInsert + str.substring(index + removeAmount, str.length());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static String formatJavaCode(String code) throws GlavaError {
        File tempFile = null;
        StringBuffer b = new StringBuffer();
        try {
            tempFile = new File("target" + File.separatorChar + "temp.java");
            tempFile.getParentFile().mkdirs();
            tempFile.createNewFile();

            PrintWriter xwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tempFile, false), "UTF-8"));
            xwriter.write(code);
            xwriter.flush();

            Jalopy jalopy = new Jalopy();
            jalopy.setFileFormat(FileFormat.DEFAULT);
            jalopy.setInput(tempFile);
            jalopy.setOutput(b);
            jalopy.format();

            xwriter.close();

            String result = null;

            if (jalopy.getState() == Jalopy.State.OK || jalopy.getState() == Jalopy.State.PARSED) {
                result = b.toString();
            } else if (jalopy.getState() == Jalopy.State.WARN) {
                result = code;// formatted with warnings
            } else if (jalopy.getState() == Jalopy.State.ERROR) {
                result = code; // could not be formatted
            }

            return result;

        } catch (Exception e) {
            throw new GlavaError("Could not format code: " + e.toString());
        } finally {
            if (tempFile != null) tempFile.delete();
        }
    }

    private void debugLog(String message, boolean printCode) {
        if (debug) {
            System.out.println("[DEBUG] " + message);
            if (printCode) System.out.println("[DEBUG] Current code: " + this.code);
        }
    }

    private void debugLog(String message) {
        debugLog(message, false);
    }

    String getCode() {
        return this.code;
    }
}