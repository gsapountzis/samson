/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package samson.jersey.core.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import samson.metadata.TypeClassPair;

public class ReflectionHelper {

    // -- TypeClassPair

    public static TypeClassPair getTypeArgumentAndClass(Type parameterizedType) throws IllegalArgumentException {
        return getTypeArgumentAndClass(parameterizedType, 0);
    }

    public static TypeClassPair getTypeArgumentAndClass(Type parameterizedType, int index) throws IllegalArgumentException {
        final Type t = getTypeArgumentOfParameterizedType(parameterizedType, index);
        if (t == null)
            return null;

        final Class<?> c = getClassOfType(t);
        if (c == null) {
            throw new IllegalArgumentException("Generic type not supported for type parameter");
        }

        return new TypeClassPair(t, c);
    }

    private static Type getTypeArgumentOfParameterizedType(Type parameterizedType, int index) {
        if (!(parameterizedType instanceof ParameterizedType)) return null;

        ParameterizedType type = (ParameterizedType)parameterizedType;
        Type[] genericTypes = type.getActualTypeArguments();

        if (genericTypes.length <= index)
            return null;

        return genericTypes[index];
    }

    private static Class<?> getClassOfType(Type type) {
        if (type instanceof Class) {
            return (Class<?>)type;
        } else if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType)type;
            Type t = arrayType.getGenericComponentType();
            if (t instanceof Class) {
                return getArrayClass((Class<?>)t);
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType subType = (ParameterizedType)type;
            Type t = subType.getRawType();
            if (t instanceof Class) {
                return (Class<?>)t;
            }
        }
        return null;
    }

    /**
     * Get Array class of component class.
     *
     * @param c the component class of the array
     * @return the array class.
     */
    public static Class<?> getArrayClass(Class<?> c) {
        try {
            Object o = Array.newInstance(c, 0);
            return o.getClass();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // -- ClassTypePair

    /**
     * A tuple consisting of a class and type of the class.
     */
    public static class ClassTypePair {
        /**
         * The class.
         */
        public final Class<?> c;

        /**
         * The type of the class.
         */
        public final Type t;

        public ClassTypePair(Class<?> c) {
            this(c, c);
        }

        public ClassTypePair(Class<?> c, Type t) {
            this.c = c;
            this.t = t;
        }
    }

    /**
     * Given a type variable resolve the Java class of that variable.
     *
     * @param c the concrete class from which all type variables are resolved
     * @param dc the declaring class where the type variable was defined
     * @param tv the type variable
     * @return the resolved Java class and type, otherwise null if the type variable
     *         could not be resolved
     */
    public static ClassTypePair resolveTypeVariable(Class<?> c, Class<?> dc, TypeVariable<?> tv) {
        return resolveTypeVariable(c, dc, tv, new HashMap<TypeVariable<?>, Type>());
    }

    private static ClassTypePair resolveTypeVariable(Class<?> c, Class<?> dc, TypeVariable<?> tv, Map<TypeVariable<?>, Type> map) {
        Type[] gis = c.getGenericInterfaces();
        for (Type gi : gis) {
            if (gi instanceof ParameterizedType) {
                // process pt of interface
                ParameterizedType pt = (ParameterizedType)gi;
                ClassTypePair ctp = resolveTypeVariable(pt, (Class<?>)pt.getRawType(), dc, tv, map);
                if (ctp != null)
                    return ctp;
            }
        }

        Type gsc = c.getGenericSuperclass();
        if (gsc instanceof ParameterizedType) {
            // process pt of class
            ParameterizedType pt = (ParameterizedType)gsc;
            return resolveTypeVariable(pt, c.getSuperclass(), dc, tv, map);
        } else if (gsc instanceof Class) {
            return resolveTypeVariable(c.getSuperclass(), dc, tv, map);
        }
        return null;
    }

    private static ClassTypePair resolveTypeVariable(ParameterizedType pt, Class<?> c, Class<?> dc, TypeVariable<?> tv, Map<TypeVariable<?>, Type> map) {
        Type[] typeArguments = pt.getActualTypeArguments();

        TypeVariable<?>[] typeParameters = c.getTypeParameters();

        Map<TypeVariable<?>, Type> submap = new HashMap<TypeVariable<?>, Type>();
        for (int i = 0; i < typeArguments.length; i++) {
            // Substitute a type variable with the Java class
            if (typeArguments[i] instanceof TypeVariable) {
                Type t = map.get(typeArguments[i]);
                submap.put(typeParameters[i], t);
            } else {
                submap.put(typeParameters[i], typeArguments[i]);
            }
        }

        if (c == dc) {
            Type t = submap.get(tv);
            if (t instanceof Class) {
                return new ClassTypePair((Class<?>)t);
            } else if (t instanceof GenericArrayType) {
                t = ((GenericArrayType)t).getGenericComponentType();
                if (t instanceof Class) {
                    c = (Class<?>)t;
                    try {
                        return new ClassTypePair(getArrayClass(c));
                    } catch (Exception e) {
                    }
                    return null;
                } else if (t instanceof ParameterizedType) {
                    Type rt = ((ParameterizedType) t).getRawType();
                    if (rt instanceof Class) {
                        c = (Class<?>) rt;
                    } else {
                        return null;
                    }
                    try {
                        return new ClassTypePair(getArrayClass(c), t);
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    return null;
                }
            } else if (t instanceof ParameterizedType) {
                pt = (ParameterizedType)t;
                if (pt.getRawType() instanceof Class) {
                    return new ClassTypePair((Class<?>)pt.getRawType(), pt);
                } else
                    return null;
            } else {
                return null;
            }
        } else {
            return resolveTypeVariable(c, dc, tv, submap);
        }
    }

    public static ClassTypePair getGenericType(
            final Class<?> concreteClass,
            final Class<?> declaringClass,
            final Class<?> c,
            final Type t) {
        if (t instanceof TypeVariable) {
            TypeVariable<?> tv = (TypeVariable<?>)t;
            ClassTypePair ct = resolveTypeVariable(concreteClass, declaringClass, tv);

            if (ct != null) {
                return ct;
            }
        } else if (t instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)t;
            final Class<?> rt = (Class<?>)pt.getRawType();
            final Type[] ptts = pt.getActualTypeArguments();
            boolean modified =  false;
            for (int i = 0; i < ptts.length; i++) {
                ClassTypePair ct = getGenericType(concreteClass, declaringClass, rt, ptts[i]);
                if (ct.t != ptts[i]) {
                    ptts[i] = ct.t;
                    modified = true;
                }
            }
            if (modified) {
                ParameterizedType rpt = new ParameterizedType() {
                    @Override
                    public Type[] getActualTypeArguments() {
                        return ptts.clone();
                    }

                    @Override
                    public Type getRawType() {
                        return pt.getRawType();
                    }

                    @Override
                    public Type getOwnerType() {
                        return pt.getOwnerType();
                    }
                };
                return new ClassTypePair(rt, rpt);
            }
        } else if (t instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType)t;
            final ClassTypePair ct = getGenericType(concreteClass, declaringClass, null, gat.getGenericComponentType());
            if (gat.getGenericComponentType() != ct.t) {
                try {
                    Class<?> ac = getArrayClass(ct.c);
                    return new ClassTypePair(ac, ac);
                } catch (Exception e) {
                }
            }
        }

        return new ClassTypePair(c, t);
    }

    private static final Class<?>[] NO_ARG = new Class<?>[0];

    public static Constructor<?> getNoargConstructor(Class<?> c) {
        try {
            return c.getConstructor(NO_ARG);
        } catch (Exception e) {
            return null;
        }
    }

}
