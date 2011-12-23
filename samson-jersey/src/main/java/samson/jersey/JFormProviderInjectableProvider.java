package samson.jersey;

import java.lang.reflect.Type;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import samson.JFormProvider;
import samson.form.FormFactory;
import samson.form.ParamsProvider;
import samson.jersey.convert.JerseyConverterPredicate;
import samson.jersey.convert.JerseyConverterProvider;
import samson.jersey.convert.JerseyMultivaluedConverterProvider;

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
public class JFormProviderInjectableProvider implements InjectableProvider<Context, Type> {

    private final HttpContext context;

    private final ParamsProvider formParams = new ParamsProvider() {

        @Override
        public MultivaluedMap<String, String> get() {
            return FormParamInjectableProvider.FormParamInjectable.getParameters(context, true);
        }
    };

    private final ParamsProvider queryParams = new ParamsProvider() {

        @Override
        public MultivaluedMap<String, String> get() {
            return QueryParamInjectableProvider.QueryParamInjectable.getParameters(context, true);
        }
    };

    private final FormFactory provider;
    private final JerseyConverterPredicate stringTypePredicate;
    private final JerseyConverterProvider converterProvider;
    private final JerseyMultivaluedConverterProvider multivaluedConverterProvider;

    public JFormProviderInjectableProvider(@Context HttpContext context) {
        this.context = context;
        this.provider = new FormFactory(formParams, queryParams);

        this.stringTypePredicate = new JerseyConverterPredicate();
        this.converterProvider = new JerseyConverterProvider();
        this.multivaluedConverterProvider = new JerseyMultivaluedConverterProvider(converterProvider);

        this.provider.setStringTypePredicate(stringTypePredicate);
        this.provider.setConverterProvider(converterProvider);
        this.provider.setExtractorProvider(multivaluedConverterProvider);
    }

    @Context
    public void setStringReaderProvider(StringReaderWorkers srw) {
        stringTypePredicate.setStringReaderProvider(srw);
        converterProvider.setStringReaderProvider(srw);
    }

    @Context
    public void setExtractorProvider(MultivaluedParameterExtractorProvider mpep) {
        stringTypePredicate.setExtractorProvider(mpep);
        multivaluedConverterProvider.setExtractorProvider(mpep);
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

    @Override
    public Injectable<JFormProvider> getInjectable(ComponentContext componentContext, Context annotation, Type type) {
        if (JFormProvider.class != type) {
            return null;
        }

        return new Injectable<JFormProvider>() {

            @Override
            public JFormProvider getValue() {
                return provider;
            }
        };
    }

}
