package samson.property;

import com.google.common.base.Preconditions;

/**
 * Lexer / Tokenizer
 */
class Tokenizer {

    private final String in;
    private int offset = -1;

    Tokenizer(String in) {
        Preconditions.checkArgument(in != null);
        this.in = in;
    }

    private int getchar() {
        if (offset < in.length()) {
            offset++;
            if (offset < in.length()) {
                return in.charAt(offset);
            }
            else {
                return -1;
            }
        }
        else {
            return -1;
        }
    }

    private void ungetc() {
        if (offset >= 0) {
            offset--;
        }
    }

    Token next(TokenContext context) {
        if (context == TokenContext.DEFAULT) {
            int ch = getchar();

            while (ch == ' ' || ch == '\t') {
                ch = getchar();
            }

            if (ch == '.') {
                return new Token(ch, offset);
            }

            if (ch == '[' || ch == ']') {
                return new Token(ch, offset);
            }

            if (ch == -1) {
                return new Token(Token.EOT, offset);
            }

            if (Character.isJavaIdentifierStart(ch)) {
                int start = offset;
                StringBuilder b = new StringBuilder();
                do {
                    b.append((char) ch);
                    ch = getchar();
                } while (Character.isJavaIdentifierPart(ch));
                ungetc();
                String id = b.toString();
                return new Token(Token.ID, start, id);
            }

            return new Token(Token.UNK, offset);
        }
        else if (context == TokenContext.KEY) {
            int ch = getchar();

            while (ch == ' ' || ch == '\t') {
                ch = getchar();
            }

            if (ch == ']' || ch == -1) {
                ungetc();
                return new Token(Token.UNK, offset + 1);
            }

            int start = offset;
            StringBuilder b = new StringBuilder();
            do {
                b.append((char) ch);
                ch = getchar();

                if (ch == ' ' || ch == '\t') {
                    break;
                }

                if (ch == ']' || ch == -1) {
                    break;
                }
            } while (true);
            ungetc();
            String key = b.toString();
            return new Token(Token.KEY, start, key);
        }
        else {
            throw new IllegalStateException();
        }
    }

}
