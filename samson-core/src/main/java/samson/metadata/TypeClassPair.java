package samson.metadata;

import java.lang.reflect.Type;

import samson.jersey.core.reflection.ReflectionHelper;

/**
 * {@link TypeClassPair} + {@link ReflectionHelper} = TypeLiteral
 */
public final class TypeClassPair {

    public final Type t;
    public final Class<?> c;

    public TypeClassPair(Type t, Class<?> c) {
        this.t = t;
        this.c = c;
    }
}