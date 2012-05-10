package samson.jersey;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import samson.form.FormBuilder;
import samson.form.FormProvider;
import samson.form.SamsonForm;
import samson.metadata.Element;

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

    private final FormProvider jForm;
    private final MetadataCache cache;

    public FormParamInjectableProvider(@Context FormProvider jForm, @Context MetadataCache cache) {
        this.jForm = jForm;
        this.cache = cache;
    }

    static class FormParamInjectable extends AbstractHttpContextInjectable<SamsonForm<?>> {
        private final FormProvider jForm;
        private final Element element;

        FormParamInjectable(FormProvider jForm, Element element) {
            this.jForm = jForm;
            this.element = element;
        }

        @Override
        public SamsonForm<?> getValue(HttpContext context) {
            MultivaluedMap<String, String> params = getParameters(context, true);
            FormBuilder builder = jForm.params(element.name, params);
            return builder.bind(element);
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
    public Injectable<SamsonForm<?>> getInjectable(ComponentContext componentContext, FormParam annotation, Parameter parameter) {
        Class<?> clazz = parameter.getParameterClass();
        if (clazz != SamsonForm.class) {
            return null;
        }

        Element element = cache.getArgumentElement(componentContext.getAccesibleObject(), parameter);
        return new FormParamInjectable(jForm, element);
    }

}