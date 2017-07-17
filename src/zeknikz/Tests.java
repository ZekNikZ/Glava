package zeknikz;

import org.apache.commons.lang3.StringEscapeUtils;

public class Tests {
    static class GlavaCLArg {
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
                System.out.println("HERE: " + this.value);
            }
        }
    }

    @SuppressWarnings("unused")
    public static final GlavaCLArg[] CLArgs = new GlavaCLArg[] {
            new GlavaCLArg("12"),
            new GlavaCLArg("12.3"),
            new GlavaCLArg("2f"),
            new GlavaCLArg("'t'"),
            new GlavaCLArg("true"),
            new GlavaCLArg("asfafdB")
    };

    private static final Object B[] = {12, 12.3d, 2f, 't', true, "asfsdfasdf"};

    @SuppressWarnings({"ResultOfMethodCallIgnored","unused"})
    public static void main (String[] args) {
        boolean b = glava_getArg(4);
        int i = glava_getArg(0);
        String s = glava_getArg(5);
        Math.pow(glava_getArg(1), glava_getArg(0));
    }

    @SuppressWarnings("unchecked")
    private static <T> T glava_getArg(int num) {
        return (T) B[num];
    }
}
