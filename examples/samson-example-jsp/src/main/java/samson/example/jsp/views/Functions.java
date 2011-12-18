package samson.example.jsp.views;

import java.util.Iterator;
import java.util.List;

import samson.JForm.Field;
import samson.JForm.Messages;

public class Functions {

    public static String messages(Object object) {
        Field field = castField(object);
        Messages messages = field.getMessages();

        String conversionError = messages.getConversionError();
        if (!(conversionError == null || conversionError.isEmpty())) {
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

    private static String join(List<String> list) {
        return join(null, list, ", ");
    }

    private static String join(String string, List<String> list) {
        return join(string, list, ", ");
    }

    private static String join(String string, List<String> list, String separator) {
        StringBuffer sb = new StringBuffer();

        if (!(string == null || string.isEmpty())) {
            sb.append(string);
            if (!(list == null || list.isEmpty())) {
                sb.append(separator);
            }
        }

        if (list != null) {
            Iterator<String> iter = list.iterator();
            if (iter.hasNext()) {
                sb.append(iter.next());
                while (iter.hasNext()) {
                    sb.append(separator);
                    sb.append(iter.next());
                }
            }
        }

        return sb.toString();
    }

}
