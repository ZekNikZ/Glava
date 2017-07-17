package zeknikz;

public class GlavaTests {

    private static final String[] testPrograms = {
        //"βχδφισΠΣΔΦΓηηΠηΣηΔηΦηΓ",
        //"βχδφισΠΣΔΦΓηηΠηΣηΔηΦηΓ\"βχδφισΠΣΔΦΓηηΠηΣηΔηΦηΓ\"βχδφισΠΣΔΦΓηηΠηΣηΔηΦηΓ",
        //"abcd'efgh'\\ijk",
        //"abc\"def\\\"ghi\"jkl",
        "Ω10)λi",
        "public static void main (String[] A) {System.out.println(\"test\");}"
    };

    public static void main (String[] args) {
        System.out.println("RESULTS:");
        for (String program : testPrograms) {
            GlavaRunner runner = new GlavaRunner(program, true, null);
            try {
                runner.convertToJavaCode();
            } catch (GlavaError e) {
                e.printStackTrace();
            }
            System.out.printf("CODE: %s\n", program);
            System.out.printf("CONVERTED CODE: %s\n", runner.getCode());
            System.out.println("RESULT:");
            try {
                runner.runCode();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            System.out.println();
        }
    }
}
