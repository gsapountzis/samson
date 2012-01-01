package samson.form;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ElementDescriptor;

import samson.metadata.BeanProperty;
import samson.metadata.Element;
import samson.metadata.TypeClassPair;

/**
 * Validator extension methods.
 */
public class ValidatorExt {

    public static ElementDescriptor getElementDescriptorDecl(Validator validator, Element element) {
        ElementDescriptor decl = null;

        if (element != Element.NULL_ELEMENT) {
            if (element instanceof BeanProperty) {
                BeanProperty property = (BeanProperty) element;
                BeanDescriptor bean = validator.getConstraintsForClass(property.beanClass);
                decl = bean.getConstraintsForProperty(property.propertyName);
            }
            else {
                // check for method parameter here
                decl = null;
            }
        }

        return decl;
    }

    public static ElementDescriptor getElementDescriptorType(Validator validator, Element element) {
        ElementDescriptor type = null;

        if (element != Element.NULL_ELEMENT) {
            TypeClassPair tcp = element.tcp;
            type = validator.getConstraintsForClass(tcp.c);
        }

        return type;
    }

    public static Set<ConstraintViolation<?>> validateDecl(Validator validator, Element element) {
        /*
         * TODO finish declaration point validation.
         *
         * This requires either getting declaration point metadata (method, parameter index)
         * or (bean class, property name) from jersey or a little help from the validator
         * (https://hibernate.onjira.com/browse/HV-549).
         */
        return Collections.emptySet();
    }

    public static Set<ConstraintViolation<Object>> validateType(Validator validator, Element element, Object value) {
        if (value == null) {
            return Collections.emptySet();
        }

        Set<ConstraintViolation<Object>> violations = Collections.emptySet();

        Class<?> clazz = element.tcp.c;
        if (Utils.isBaseType(clazz)) {
            // do nothing
        }
        else if (Collection.class.isAssignableFrom(clazz)) {
            // cascade to each item
        }
        else if (Map.class.isAssignableFrom(clazz)) {
            // cascade to each entry
        }
        else {
            // bean validation
            violations = validator.validate(value);
        }

        return violations;
    }

}
