package samson.form;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Property {

    /**
     * A path to an object in an object graph.
     */
    static class Path implements Iterable<Node> {

        /** Immutable */
        private final List<Node> nodes;

        public Path() {
            this.nodes = new ArrayList<Node>();
        }

        /**
         * Create a new root path.
         */
        public static Path createRoot(String name) {
            Node node = Node.createPrefix(name);

            Path path = new Path();
            path.nodes.add(node);
            return path;
        }

        /**
         * Create a new path from an EL property.
         */
        public static Path createPath(String property) throws ParseException {
            List<Node> nodes = Parser.parse(property);

            Path path = new Path();
            for (Node node : nodes) {
                path.nodes.add(node);
            }
            return path;
        }

        public Path subpath(int index) {
            int size = nodes.size();
            if (index < 0) {
                throw new IllegalArgumentException("Index out of bounds");
            }
            if (index > size) {
                throw new IllegalArgumentException("Index out of bounds");
            }

            Path path = new Path();
            for (int i = index; i < size; i++) {
                path.nodes.add(nodes.get(i));
            }
            return path;
        }

        public boolean isEmpty() {
            return nodes.isEmpty();
        }

        public int size() {
            return nodes.size();
        }

        @Override
        public Iterator<Node> iterator() {
            return nodes.iterator();
        }

        @Override
        public int hashCode() {
            return (nodes == null) ? 0 : nodes.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }

            Path other = (Path) obj;

            if (nodes == null) {
                if (other.nodes != null) {
                    return false;
                }
            } else if (!nodes.equals(other.nodes)) {
                return false;
            }

            return true;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Node node : nodes) {
                sb.append(node);
            }
            return sb.toString();
        }
    }

    /**
     * A node in the path to an object in an object graph.
     *
     * The node keeps additional information about the parsing of the EL property,
     * for pretty printing but only the name is significant.
     */
    static class Node {

        /** The immutable name of this node: prefix (root node) / dot suffix / index suffix */
        private final String name;

        /** The part of the property name that matches this node */
        private final String match;

        private final boolean prefix;

        /** This value is accessed with a dot syntax i.e. "bean.property" */
        private final boolean dot;

        /** This value is accessed with an index syntax i.e. "bean[property]" */
        private final boolean index;

        private Node(String name, String match, boolean prefix, boolean dot, boolean index) {
            this.name = name;
            this.match = match;
            this.prefix = prefix;
            this.dot = dot;
            this.index = index;
        }

        static Node createPrefix(String name) {
            return new Node(name, name, true, false, false);
        }

        static Node createDotSuffix(String name, String match) {
            return new Node(name, match, false, true, false);
        }

        static Node createIndexSuffix(String name, String match) {
            return new Node(name, match, false, false, true);
        }

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return (name == null) ? 0 : name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }

            Node other = (Node) obj;

            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            }
            else if (!name.equals(other.name)) {
                return false;
            }

            return true;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (prefix) {
                if (name != null) {
                    sb.append(name);
                }
            }
            else {
                if (dot)
                    sb.append(".").append(name);
                else if (index)
                    sb.append("[").append(name).append("]");
            }
            return sb.toString();
        }
    }

    private static final Parser Parser = new Parser();

    private static class Parser {

        private static final Pattern PREFIX_PATTERN = Pattern.compile("[^\\.\\[]+");        // Identifier
        private static final Pattern DOT_PATTERN = Pattern.compile("\\.([^\\.\\[]+)");      // '.' Identifier
        private static final Pattern INDEX_PATTERN = Pattern.compile("\\[([^\\]]+)\\]");    // '[' Literal ']'

        Node getPrefix(String path) throws ParseException {

            if (Utils.isNullOrEmpty(path)) {
                return null;
            }

            Matcher matcher = PREFIX_PATTERN.matcher(path);
            if (matcher.lookingAt()) {
                String match = matcher.group();
                return Node.createPrefix(match);
            }
            else {
                throw new ParseException("Cannot parse prefix", -1);
            }
        }

        Node getNextSuffix(String suffix) throws ParseException {

            if (Utils.isNullOrEmpty(suffix)) {
                return null;
            }

            if (suffix.charAt(0) == '.') {
                Matcher matcher = DOT_PATTERN.matcher(suffix);
                if (matcher.lookingAt()) {
                    String match = matcher.group();
                    String name = matcher.group(1);
                    return Node.createDotSuffix(name, match);
                }
                else {
                    throw new ParseException("Cannot parse suffix", -1);
                }
            }
            else if (suffix.charAt(0) == '[') {
                Matcher matcher = INDEX_PATTERN.matcher(suffix);
                if (matcher.lookingAt()) {
                    String match = matcher.group();
                    String name = matcher.group(1);
                    return Node.createIndexSuffix(name, match);
                }
                else {
                    throw new ParseException("Cannot parse suffix", -1);
                }
            }
            else {
                throw new ParseException("Cannot parse suffix", -1);
            }
        }

        List<Node> parse(String property) throws ParseException {
            List<Node> nodes = new ArrayList<Node>();

            Node node = getPrefix(property);
            if (node != null) {
                nodes.add(node);
                String suffix = property.substring(node.match.length());

                while ((node = getNextSuffix(suffix)) != null) {
                    nodes.add(node);
                    suffix = suffix.substring(node.match.length());
                }
            }

            return nodes;
        }

    }

}
