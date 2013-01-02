package samson.property;

import java.text.ParseException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;

public class ParsePath implements Iterable<ParseNode> {

    /** Immutable */
    private final List<ParseNode> nodes;

    private ParsePath(List<ParseNode> nodes) {
        Preconditions.checkArgument(nodes != null);
        this.nodes = Collections.unmodifiableList(nodes);
    }

    public static ParsePath of() throws ParseException {
        return new ParsePath(Collections.<ParseNode>emptyList());
    }

    public static ParsePath of(String property) throws ParseException {
        return new ParsePath(new Parser(property).parse());
    }

    public ParsePath subPath(int index) {
        return new ParsePath(nodes.subList(index, nodes.size()));
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public int size() {
        return nodes.size();
    }

    @Override
    public Iterator<ParseNode> iterator() {
        return nodes.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ParseNode node : nodes) {
            sb.append(node);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return nodes.hashCode();
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

        ParsePath other = (ParsePath) obj;

        return nodes.equals(other.nodes);
    }

}
