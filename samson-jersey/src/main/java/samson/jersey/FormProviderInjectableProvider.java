package samson.jersey;

import java.lang.reflect.Type;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import samson.form.FormProvider;
import samson.form.ParamsProvider;
import samson.jersey.convert.JerseyConverterProvider;
import samson.jersey.convert.multivalued.JerseyMultivaluedConverterProvider;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import com.sun.jersey.spi.StringReaderWorkers;
import com.sun.jersey.spi.inject.ConstrainedTo;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.jersey.spi.inject.ServerSide;

@ConstrainedTo(ServerSide.class)
public class FormProviderInjectableProvider implements InjectableProvider<Context, Type> {

    private final HttpContext context;

    private final ParamsProvider formParams = new ParamsProvider() {

        @Override
        public MultivaluedMap<String, String> get() {
            return FormParamInjectableProvider.getParameters(context, true);
        }
    };

    private final ParamsProvider queryParams = new ParamsProvider() {

        @Override
        public MultivaluedMap<String, String> get() {
            return QueryParamInjectableProvider.getParameters(context, true);
        }
    };

    private final FormProvider provider;
    private final JerseyConverterProvider converterProvider;
    private final JerseyMultivaluedConverterProvider multivaluedConverterProvider;

    public FormProviderInjectableProvider(@Context HttpContext context) {
        this.context = context;
        this.converterProvider = new JerseyConverterProvider();
        this.multivaluedConverterProvider = new JerseyMultivaluedConverterProvider(converterProvider);
        this.provider = new FormProvider(formParams, queryParams, converterProvider, multivaluedConverterProvider);
    }

    @Context
    public void setStringReaderProvider(StringReaderWorkers srw) {
        converterProvider.setStringReaderProvider(srw);
    }

    @Context
    public void setExtractorProvider(MultivaluedParameterExtractorProvider mpep) {
        multivaluedConverterProvider.setExtractorProvider(mpep);
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

    @Override
    public Injectable<FormProvider> getInjectable(ComponentContext componentContext, Context annotation, Type type) {
        if (FormProvider.class != type) {
            return null;
        }

        return new Injectable<FormProvider>() {

            @Override
            public FormProvider getValue() {
                return provider;
            }
        };
    }

}
