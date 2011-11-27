package samson.jersey.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import samson.JFormProvider;
import samson.form.FormFactory;
import samson.jersey.SamsonMultivaluedExtractorProvider;
import samson.jersey.SamsonMultivaluedTypePredicate;
import samson.jersey.SamsonStringReaderWorkers;

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
             * The following list is taken from META-INF/services of jersey-server
             * keeping the order and excluding the JAXB StringReader.
             */
            this.readers = new LinkedHashSet<StringReaderProvider<?>>();
            this.readers.add(new StringReaderProviders.TypeFromStringEnum());
            this.readers.add(new StringReaderProviders.TypeValueOf());
            this.readers.add(new StringReaderProviders.TypeFromString());
            this.readers.add(new StringReaderProviders.StringConstructor());
            this.readers.add(new StringReaderProviders.DateProvider());
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

        SamsonMultivaluedTypePredicate samsonStp = new SamsonMultivaluedTypePredicate();
        samsonStp.setStringReaderProvider(srw);
        samsonStp.setExtractorProvider(mpep);

        SamsonStringReaderWorkers samsonSrw = new SamsonStringReaderWorkers(srw);
        SamsonMultivaluedExtractorProvider samsonMpep = new SamsonMultivaluedExtractorProvider(mpep);

        FormFactory provider = new FormFactory();
        provider.setStringTypePredicate(samsonStp);
        provider.setConverterProvider(samsonSrw);
        provider.setExtractorProvider(samsonMpep);

        return provider;
    }
}
