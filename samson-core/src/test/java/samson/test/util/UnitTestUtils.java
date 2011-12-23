package samson.test.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import samson.JFormProvider;
import samson.form.FormFactory;
import samson.jersey.convert.JerseyConverterPredicate;
import samson.jersey.convert.JerseyConverterProvider;
import samson.jersey.convert.JerseyMultivaluedConverterProvider;

import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorFactory;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import com.sun.jersey.server.impl.model.parameter.multivalued.StringReaderProviders;
import com.sun.jersey.spi.StringReader;
import com.sun.jersey.spi.StringReaderProvider;
import com.sun.jersey.spi.StringReaderWorkers;

public class UnitTestUtils {

    private static class TestStringReaderFactory implements StringReaderWorkers {

        private Set<StringReaderProvider<?>> readers;

        public TestStringReaderFactory() {
            /**
             * Cannot use Jersey ServiceFinder or Java 6 ServiceLoader beacuse
             * {@link JAXBStringReaderProviders.RootElementProvider} uses injection
             * and cannot be instantiated with a no-arg constructor.
             *
             * The following list is taken from META-INF/services of jersey-server,
             * excluding the JAXB StringReader. The order is changed to put specific
             * providers (e.g. Date) before generic ones (e.g. StringConstructor).
             */
            this.readers = new LinkedHashSet<StringReaderProvider<?>>();
            this.readers.add(new StringReaderProviders.DateProvider());
            this.readers.add(new StringReaderProviders.TypeFromStringEnum());
            this.readers.add(new StringReaderProviders.TypeValueOf());
            this.readers.add(new StringReaderProviders.TypeFromString());
            this.readers.add(new StringReaderProviders.StringConstructor());
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> StringReader<T> getStringReader(Class<T> type, Type genericType, Annotation[] annotations) {
            for (StringReaderProvider<?> srp : readers) {
                StringReader<T> sr = (StringReader<T>) srp.getStringReader(type, genericType, annotations);
                if (sr != null)
                    return sr;
            }
            return null;
        }

    }

    public static JFormProvider createJFormProvider() {
        StringReaderWorkers srw = new TestStringReaderFactory();
        MultivaluedParameterExtractorProvider mpep = new MultivaluedParameterExtractorFactory(srw);

        JerseyConverterPredicate samsonStp = new JerseyConverterPredicate();
        samsonStp.setStringReaderProvider(srw);
        samsonStp.setExtractorProvider(mpep);

        JerseyConverterProvider samsonSrw = new JerseyConverterProvider();
        samsonSrw.setStringReaderProvider(srw);

        JerseyMultivaluedConverterProvider samsonMpep = new JerseyMultivaluedConverterProvider(samsonSrw);
        samsonMpep.setExtractorProvider(mpep);

        FormFactory provider = new FormFactory();
        provider.setStringTypePredicate(samsonStp);
        provider.setConverterProvider(samsonSrw);
        provider.setExtractorProvider(samsonMpep);

        return provider;
    }
}
