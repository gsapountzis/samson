package samson.jersey;

import java.lang.reflect.Type;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import samson.Element;
import samson.JForm;
import samson.JFormProvider;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.reflection.ReflectionHelper.TypeClassPair;
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
    }

    static MultivaluedMap<String, String> getParameters(HttpContext context, boolean decode) {
        Form form = (Form) context.getProperties().get(FormDispatchProvider.FORM_PROPERTY);
        if (form == null) {
            form = getForm(context);
            context.getProperties().put(FormDispatchProvider.FORM_PROPERTY, form);
        }
        return form;
    }

    private static Form getForm(HttpContext context) {
        final HttpRequestContext r = context.getRequest();
        if (r.getMethod().equals("GET")) {
            throw new IllegalStateException("Form with HTTP method GET");
        }

        if (!MediaTypes.typeEquals(MediaType.APPLICATION_FORM_URLENCODED_TYPE, r.getMediaType())) {
            throw new IllegalStateException("Form with HTTP content-type other than x-www-form-urlencoded");
        }

        return r.getFormParameters();
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable<JForm<?>> getInjectable(ComponentContext componentContext, FormParam annotation, Parameter parameter) {
        Class<?> clazz = parameter.getParameterClass();
        if (clazz != JForm.class) {
            return null;
        }

        Element element = getArgumentElement(parameter);
        return new FormParamInjectable(jForm, element);
    }

    static Element getArgumentElement(Parameter parameter) {
        Type type = parameter.getParameterType();

        TypeClassPair tcp = ReflectionHelper.getTypeArgumentAndClass(type);
        if (tcp == null) {
            throw new ContainerException("Parameterized type without type arguement");
        }

        return new Element(
                parameter.getAnnotations(),
                tcp.t, tcp.c,
                parameter.getSourceName(),
                parameter.isEncoded(),
                parameter.getDefaultValue());
    }

}