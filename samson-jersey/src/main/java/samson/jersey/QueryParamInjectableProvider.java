package samson.jersey;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import samson.form.FormBuilder;
import samson.form.FormProvider;
import samson.form.SamsonForm;
import samson.metadata.Element;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.ConstrainedTo;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.jersey.spi.inject.ServerSide;

@ConstrainedTo(ServerSide.class)
public class QueryParamInjectableProvider implements InjectableProvider<QueryParam, Parameter> {

    private final FormProvider jForm;
    private final MetadataCache cache;

    public QueryParamInjectableProvider(@Context FormProvider jForm, @Context MetadataCache cache) {
        this.jForm = jForm;
        this.cache = cache;
    }

    static class QueryParamInjectable extends AbstractHttpContextInjectable<SamsonForm<?>> {
        private final FormProvider jForm;
        private final Element element;

        QueryParamInjectable(FormProvider jForm, Element element) {
            this.jForm = jForm;
            this.element = element;
        }

        @Override
        public SamsonForm<?> getValue(HttpContext context) {
            MultivaluedMap<String, String> params = getParameters(context, true);
            FormBuilder builder = jForm.params(element.jaxrs.name, params);
            return builder.bind(element);
        }
    }

    static MultivaluedMap<String, String> getParameters(HttpContext context, boolean decode) {
        return context.getUriInfo().getQueryParameters(decode);
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable<SamsonForm<?>> getInjectable(ComponentContext componentContext, QueryParam annotation, Parameter parameter) {
        Class<?> clazz = parameter.getParameterClass();
        if (clazz != SamsonForm.class) {
            return null;
        }

        Element element = cache.getArgumentElement(componentContext.getAccesibleObject(), parameter);
        return new QueryParamInjectable(jForm, element);
    }

}