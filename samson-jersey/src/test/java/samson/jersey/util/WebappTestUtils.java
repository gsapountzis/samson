package samson.jersey.util;

import static org.junit.Assert.assertTrue;

import java.net.URI;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.WebApplication;

public class WebappTestUtils {
    public static final URI BASE_URI = URI.create("test:/base/");

    public static WebApplication createWepapp(Class<?>... classes) {

        ResourceConfig resourceConfig = new DefaultResourceConfig(classes);
        WebApplication webapp = new WebApplicationImpl();
        webapp.initiate(resourceConfig);

        return webapp;
    }

    public static WebResource resource(WebApplication webapp) {
        boolean checkStatus = false;

        ClientHandler clientHandler = new WebappTestClientHandler(webapp, BASE_URI);

        Client client = new Client(clientHandler);

        if (checkStatus) {
            client.addFilter(new ClientFilter() {
                @Override
                public ClientResponse handle(ClientRequest request) {
                    ClientResponse response = super.getNext().handle(request);
                    assertTrue("Status: " + response.getStatus(), response.getStatus() < 300);
                    return response;
                }
            });
        }

        WebResource resource = client.resource(BASE_URI);
        return resource;
    }

}
