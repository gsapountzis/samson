package samson.metadata;

import java.lang.reflect.Type;

/**
 * TypeClassPair + ReflectionHelper = TypeLiteral
 */
public final class TypeClassPair {

    public final Type t;
    public final Class<?> c;

    public TypeClassPair(Type t, Class<?> c) {
        this.t = t;
        this.c = c;
    }
}