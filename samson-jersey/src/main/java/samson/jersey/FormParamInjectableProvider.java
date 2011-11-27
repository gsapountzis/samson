package samson.jersey;

import java.lang.reflect.Type;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import samson.JForm;
import samson.JFormProvider;
import samson.jersey.core.reflection.ReflectionHelper;
import samson.metadata.Element;
import samson.metadata.TypeClassPair;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.server.impl.model.method.dispatch.FormDispatchProvider;
import com.sun.jersey.spi.inject.ConstrainedTo;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.jersey.spi.inject.ServerSide;

@ConstrainedTo(ServerSide.class)
public class FormParamInjectableProvider implements InjectableProvider<FormParam, Parameter> {

    private final JFormProvider jForm;

    public FormParamInjectableProvider(@Context JFormProvider jForm) {
        this.jForm = jForm;
    }

    static class FormParamInjectable extends AbstractHttpContextInjectable<JForm<?>> {
        private final JFormProvider jForm;
        private final Element element;

        FormParamInjectable(JFormProvider jForm, Element element) {
            this.jForm = jForm;
            this.element = element;
        }

        @Override
        public JForm<?> getValue(HttpContext context) {
            MultivaluedMap<String, String> params = getParameters(context, true);
            return jForm.bind(element).params(element.name, params);
        }

        static MultivaluedMap<String, String> getParameters(HttpContext context, boolean decode) {
            Form form = (Form) context.getProperties().get(FormDispatchProvider.FORM_PROPERTY);
            if (form == null) {
                form = getForm(context);
                if (form == null) {
                    throw new IllegalStateException();
                }
                context.getProperties().put(FormDispatchProvider.FORM_PROPERTY, form);
            }
            return form;
        }

        static Form getForm(HttpContext context) {
            final HttpRequestContext r = context.getRequest();
            if (r.getMethod().equals("GET"))
                return null;

            if (!MediaTypes.typeEquals(MediaType.APPLICATION_FORM_URLENCODED_TYPE, r.getMediaType()))
                return null;

            return r.getFormParameters();
        }
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable<JForm<?>> getInjectable(ComponentContext componentContext, FormParam annotation, Parameter parameter) {
        Element element = getArgumentElement(parameter);
        if (element == null) {
            return null;
        }

        return new FormParamInjectable(jForm, element);
    }

    static Element getArgumentElement(Parameter parameter) {
        Class<?> clazz = parameter.getParameterClass();
        Type type = parameter.getParameterType();

        if (clazz != JForm.class) {
            return null;
        }

        TypeClassPair tcp = ReflectionHelper.getTypeArgumentAndClass(type);
        if (tcp == null) {
            throw new ContainerException("Parameterized type without type arguement");
        }

        return new Element(
                parameter.getAnnotations(),
                tcp,
                parameter.getSourceName(),
                parameter.isEncoded(),
                parameter.getDefaultValue());
    }
}