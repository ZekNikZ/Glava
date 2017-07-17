package zeknikz;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

public class Glava {
    private static final String GLAVA_ENCODING = "ISO-8859-7";
    private static final String HELP_STRING =
            "GLAVA COMMAND-LINE REFERENCE\n" +
            "----------------------------\n" +
            "-help, -h, -?:\n" +
            "  Display the command-line reference sheet.\n" +
            "-file [file], -f [file]:\n" +
            "  Open a file and interpret it as Glava code in UTF-8.\n" +
            "-gfile [file], -gf [file]:\n" +
            "  Open a file and interpret it as Glava code in " + GLAVA_ENCODING + ".\n" +
            "-code [code], -c [code]:\n" +
            "  Run a UTF-8 string as Glava code.\n" +
            "-debug, -d:\n" +
            "  Enable debug mode.\n";

    private static boolean debugMode = false;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("ERROR: Too few arguments! Use -help for help.");
            return;
        }
        String code;
        argLoop:
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-help":
                case "-h":
                case "-?":
                    System.out.println(HELP_STRING);
                    break;

                case "-file":
                case "-f":
                    if (i + 1 >= args.length) {
                        System.out.println("ERROR: Missing an argument! Use -help for help.");
                        return;
                    }
                    code = readFile(args[++i], "UTF-8");
                    runCode(code, debugMode, Arrays.copyOfRange(args, i + 1, args.length));
                    break argLoop;

                case "-gfile":
                case "-gf":
                    if (i + 1 >= args.length) {
                        System.out.println("ERROR: Missing an argument! Use -help for help.");
                        return;
                    }
                    code = readFile(args[++i], GLAVA_ENCODING);
                    runCode(code, debugMode, Arrays.copyOfRange(args, i + 1, args.length));
                    break argLoop;

                case "-code":
                case "-c":
                    if (i + 1 >= args.length) {
                        System.out.println("ERROR: Missing an argument! Use -help for help.");
                        return;
                    }
                    runCode(args[++i], debugMode, Arrays.copyOfRange(args, i + 1, args.length));
                    break argLoop;

                case "-debug":
                case "-d":
                    debugMode = true;
                    System.out.println("[DEBUG] Debug mode enabled.");
                    break;

                default:
                    System.out.println("ERROR: Invalid argument! Use -help for help.");
                    break;
            }
        }
    }

    private static String readFile(String fileName, String encoding) {
        try {
            File file = new File(fileName);
            FileInputStream fr = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            int fileLength = fr.read(data);
            fr.close();
            if (fileLength == -1) return null;
            return new String(data, encoding);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void runCode(String code, boolean debug, String[] args) {
        GlavaRunner runner = new GlavaRunner(code, debug, args);
        try {
            runner.convertToJavaCode();
            runner.runCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// TODO: FANCY COMMAND-LINE ARGUMENTS
// TODO: MORE SHORTHANDS
// TODO: CLEAN UP CODE