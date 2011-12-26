package samson.example.jsp.views;

import java.util.Iterator;
import java.util.List;

import samson.JForm;
import samson.JForm.Field;
import samson.JForm.Messages;

public class Functions {

    public static JForm<?> path(JForm<?> form, String path) {
        return form.path(path);
    }

    public static String infos(Object object) {
        Field field = castField(object);
        Messages messages = field.getMessages();

        String conversionInfo = messages.getConversionInfo();
        return join(join(conversionInfo, messages.getValidationInfos()), messages.getInfos());
    }

    public static String errors(Object object) {
        Field field = castField(object);
        Messages messages = field.getMessages();

        String conversionError = messages.getConversionError();
        if (!isNullOrEmpty(conversionError)) {
            return conversionError;
        }
        else {
            return join(join(messages.getValidationErrors()), messages.getErrors());
        }
    }

    private static Field castField(Object object) {
        if (object instanceof Field) {
            return (Field) object;
        }
        else {
            throw new IllegalArgumentException("Parameter must be of type Field");
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
