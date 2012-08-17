package samson.example.jsp.views;

import java.util.Iterator;
import java.util.List;

import samson.form.FormNode;

public class Functions {

    public static String infos(FormNode node) {
        String conversionInfo = node.getConversionInfo();
        return join(join(conversionInfo, node.getValidationInfos()), node.getInfos());
    }

    public static String errors(FormNode node) {
        String conversionError = node.getConversionError();
        if (!isNullOrEmpty(conversionError)) {
            return conversionError;
        }
        else {
            return join(join(node.getValidationErrors()), node.getErrors());
        }
    }

    private static boolean isNullOrEmpty(String s) {
        return (s == null || s.isEmpty());
    }

    private static String join(List<String> list) {
        return join(null, list, ", ");
    }

    private static String join(String string, List<String> list) {
        return join(string, list, ", ");
    }

    private static String join(String string, List<String> list, String separator) {
        StringBuilder sb = new StringBuilder();

        if (!isNullOrEmpty(string)) {
            sb.append(string);
            if (!list.isEmpty()) {
                sb.append(separator);
            }
        }

        Iterator<String> iter = list.iterator();
        if (iter.hasNext()) {
            sb.append(iter.next());
            while (iter.hasNext()) {
                sb.append(separator);
                sb.append(iter.next());
            }
        }

        return sb.toString();
    }

}
