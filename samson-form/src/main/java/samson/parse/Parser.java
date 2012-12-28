package samson.parse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser
 *
 * P  →  P [ key ]
 *    |  P . id
 *    |  id
 *
 * P  →  id R
 * R  →  [ key ] R
 *    |  . id R
 *    |  ϵ
 *
 */
class Parser {

    private final Tokenizer tokenizer;

    private Token lookahead = new Token(Token.SOT, -1);

    Parser(String in) {
        this.tokenizer = new Tokenizer(in);
    }

    // -- Lookahead

    private <T extends Token> T match(int token) throws ParseException {
        return match(token, TokenContext.DEFAULT);
    }

    @SuppressWarnings("unchecked")
    private <T extends Token> T match(int token, TokenContext context) throws ParseException {
        if (lookahead.type == token) {
            return (T) move(context);
        }
        else {
            throw new ParseException("Unexpected input", lookahead.offset);
        }
    }

    private Token move(TokenContext context) {
        Token current = lookahead;
        lookahead = tokenizer.next(context);
        return current;
    }

    // -- Grammar

    List<ParseNode> parse() throws ParseException {
        match(Token.SOT);
        List<ParseNode> path = path();
        match(Token.EOT);
        return path;
    }

    private List<ParseNode> path() throws ParseException {
        List<ParseNode> nodes = new ArrayList<ParseNode>();

        ParseNode prefix = prefix();
        nodes.add(prefix);
        while (true) {
            if (lookahead.type == '.') {
                ParseNode suffix = dot();
                nodes.add(suffix);
            }
            else if (lookahead.type == '[') {
                ParseNode suffix = index();
                nodes.add(suffix);
            }
            else {
                break;
            }
        }

        return nodes;
    }

    private ParseNode prefix() throws ParseException {
        Token id = match(Token.ID);
        return ParseNode.newPrefix(id.value, id.offset);
    }

    private ParseNode dot() throws ParseException {
        Token dot = match('.');
        Token id = match(Token.ID);
        return ParseNode.newDotSuffix(id.value, dot.offset);
    }

    private ParseNode index() throws ParseException {
        Token lsb = match('[', TokenContext.KEY);
        Token key = match(Token.KEY);
        match(']');
        return ParseNode.newIndexSuffix(key.value, lsb.offset);
    }

}
