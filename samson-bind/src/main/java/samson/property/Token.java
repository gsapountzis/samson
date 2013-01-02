package samson.property;

class Token {

    static final int SOT = 2;       // start of text
    static final int EOT = 3;       // end of text
    static final int DOT = '.';
    static final int LSB = '[';     // left square bracket
    static final int RSB = ']';     // right square bracket
    static final int ID  = 256;     // java identifier
    static final int KEY = 257;     // map key value
    static final int UNK = 258;     // unkown character

    final int type;
    final int offset;

    // union
    final String value;

    Token(int type, int offset) {
        this(type, offset, null);
    }

    Token(int type, int offset, String value) {
        this.type = type;
        this.offset = offset;
        this.value = value;
    }

}
