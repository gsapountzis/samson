package samson.form;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ElementDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.metadata.BeanProperty;
import samson.metadata.Element;
import samson.metadata.MethodParameter;
import samson.metadata.TypeClassPair;
import samson.utils.Utils;

/**
 * Validator extension methods.
 */
public class ValidatorExt {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorExt.class);

    public static ElementDescriptor getElementDescriptorDecl(Validator validator, Element element) {
        if (element != Element.NULL_ELEMENT) {
            if (element instanceof MethodParameter) {
                throw new UnsupportedOperationException();
            }
            else if (element instanceof BeanProperty) {
                BeanProperty property = (BeanProperty) element;
                Class<?> beanType = property.beanTcp.c;
                String propertyName = property.propertyName;

                BeanDescriptor bean = validator.getConstraintsForClass(beanType);
                return bean.getConstraintsForProperty(propertyName);
            }
            else {
                throw new UnsupportedOperationException();
            }
        }
        else {
            return null;
        }
    }

    public static ElementDescriptor getElementDescriptorType(Validator validator, Element element) {
        if (element != Element.NULL_ELEMENT) {
            TypeClassPair tcp = element.tcp;
            return validator.getConstraintsForClass(tcp.c);
        }
        else {
            return null;
        }
    }

    public static Set<ConstraintViolation<Object>> validateDecl(Validator validator, Element element, Object value) {
        return Collections.emptySet();
    }

    public static Set<ConstraintViolation<Object>> validateType(Validator validator, Element element, Object value) {
        if (value == null) {
            return Collections.emptySet();
        }

        Class<?> clazz = element.tcp.c;
        if (Utils.isBaseType(clazz)) {
            // do nothing
            return Collections.emptySet();
        }
        else if (Collection.class.isAssignableFrom(clazz)) {
            // cascade to each item
            LOGGER.warn("Cannot validate root-level collections");
            return Collections.emptySet();
        }
        else if (Map.class.isAssignableFrom(clazz)) {
            // cascade to each entry
            LOGGER.warn("Cannot validate root-level maps");
            return Collections.emptySet();
        }
        else {
            // bean validation
            return validator.validate(value);
        }
    }

}
