package samson.example.jsp.views;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import samson.form.FormNode;

public class Functions {

    public static String urlEncode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }

    public static String messages(FormNode node) {
        return errors(node);
    }

//  def multiMessages(nodes: FormNode*) = nodes.map(messages(_)).filter(!isNullOrEmpty(_)).mkString(", ")

    public static String multiMessages(FormNode... nodes) {
        List<String> multiMessages = new ArrayList<String>();
        for (FormNode node : nodes) {
            String messages = messages(node);
            if (!isNullOrEmpty(messages)) {
                multiMessages.add(messages);
            }
        }
        return join(multiMessages);
    }

//  def multiError(nodes: FormNode*) = nodes.map(_.isError).reduce(_ || _)

    public static boolean multiError(FormNode... nodes) {
        for (FormNode node : nodes) {
            if (node.isError()) {
                return true;
            }
        }
        return false;
    }

    public static String multiMessages2(FormNode node0, FormNode node1) {
        return multiMessages(new FormNode[]{node0, node1});
    }

    public static boolean multiError2(FormNode node0, FormNode node1) {
        return multiError(new FormNode[]{node0, node1});
    }

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
