package samson.parse;

import com.google.common.base.Preconditions;

public class ParseNode {

    final String name;

    final int offset;

    ParseNode(String name, int offset) {
        Preconditions.checkArgument(name != null && !name.isEmpty());
        this.name = name;
        this.offset = offset;
    }

    static ParseNode newPrefix(String name) {
        return new ParseNode(name, -1);
    }

    static ParseNode newPrefix(String name, int offset) {
        return new ParseNode(name, offset);
    }

    static ParseNode newDotSuffix(String name, int offset) {
        return new DotNode(name, offset);
    }

    static ParseNode newIndexSuffix(String name, int offset) {
        return new IndexNode(name, offset);
    }

    public String getName() {
        return name;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
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

        ParseNode other = (ParseNode) obj;

        return name.equals(other.name);
    }

    private static class DotNode extends ParseNode {
        DotNode(String name, int offset) {
            super(name, offset);
        }

        @Override
        public String toString() {
            return "." + name;
        }
    }

    private static class IndexNode extends ParseNode {
        IndexNode(String name, int offset) {
            super(name, offset);
        }

        @Override
        public String toString() {
            return "[" + name + "]";
        }
    }

}