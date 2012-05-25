package samson.jersey;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;

import samson.form.FormBuilder;
import samson.form.FormProvider;
import samson.form.SamsonForm;
import samson.metadata.Element;
import samson.metadata.TypeClassPair;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.spi.inject.ConstrainedTo;
import com.sun.jersey.spi.inject.ServerSide;

@ConstrainedTo(ServerSide.class)
@Produces("application/x-www-form-urlencoded")
@Consumes("application/x-www-form-urlencoded")
public class FormBeanProvider extends AbstractMessageReaderWriterProvider<SamsonForm<?>> {

    private final Providers providers;
    private final FormProvider jForm;

    public FormBeanProvider(@Context Providers providers, @Context FormProvider jForm) {
        this.providers = providers;
        this.jForm = jForm;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return (type == SamsonForm.class);
    }

    @Override
    public SamsonForm<?> readFrom(
            Class<SamsonForm<?>> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream)
                    throws IOException, WebApplicationException {

        ReflectionHelper.TypeClassPair tcp = ReflectionHelper.getTypeArgumentAndClass(genericType);
        if (tcp == null) {
            throw new ContainerException("Parameterized type without type arguement");
        }
        TypeClassPair samsonTcp = new TypeClassPair(tcp.t, tcp.c);
        Element element = new Element(annotations, samsonTcp);

        MessageBodyReader<Form> formProvider = providers.getMessageBodyReader(Form.class, Form.class, annotations, mediaType);
        if (formProvider == null) {
            throw new IllegalStateException("Cannot find default form entity provider");
        }
        Form form = formProvider.readFrom(Form.class, Form.class, annotations, mediaType, httpHeaders, entityStream);

        FormBuilder builder = jForm.params(form);
        return builder.bind(element);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return false;
    }

    @Override
    public void writeTo(
            SamsonForm<?> t,
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream)
                    throws IOException, WebApplicationException {

        throw new UnsupportedOperationException();
    }

}
