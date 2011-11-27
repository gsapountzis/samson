package samson.example.jsp.resources;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import samson.JForm;
import samson.JFormProvider;
import samson.example.jsp.domain.Product;

import com.sun.jersey.api.view.Viewable;

@Path("/products")
public class ProductsResource {

    @Context
    private JFormProvider jForm;

    @GET
    public Response list() {
        Collection<Product> products = Repository.get().getProducts();

        Viewable view = Views.products.list(products);
        return Response.ok(view).build();
    }

    @Path("new")
    @GET
    public Response create() {
        Viewable view = Views.products.create(jForm.wrap(Product.class));
        return Response.ok(view).build();
    }

    /**
     * Create product, shows usage as a resource method parameter.
     */
    @POST
    public Response save(@FormParam("product") JForm<Product> productForm) {

        if (productForm.hasErrors()) {
            Viewable view = Views.products.create(productForm);
            return Response.status(BAD_REQUEST).entity(view).build();
        }

        Product product = productForm.get();
        Long id = Repository.get().createProduct(product);

        URI path = Paths.products.view(id);
        return Response.seeOther(path).build();
    }

    @Path("{id}")
    @GET
    public Response view(@PathParam("id") Long id) {
        Product product = Repository.get().findProduct(id);
        if (product == null) {
            return Response.status(NOT_FOUND).build();
        }

        Viewable view = Views.products.view(id, product);
        return Response.ok(view).build();
    }

    @Path("{id}/edit")
    @GET
    public Response edit(@PathParam("id") Long id) {
        Product product = Repository.get().findProduct(id);
        if (product == null) {
            return Response.status(NOT_FOUND).build();
        }

        Viewable view = Views.products.edit(id, jForm.wrap(Product.class, product));
        return Response.ok(view).build();
    }

    /**
     * Update product, shows usage within the resource method body.
     */
    @Path("{id}")
    @POST
    public Response update(@PathParam("id") Long id) {

        JForm<Product> productForm = jForm.bind(Product.class).form("product");

        if (productForm.hasErrors()) {
            Viewable view = Views.products.edit(id, productForm);
            return Response.status(BAD_REQUEST).entity(view).build();
        }

        Product product = productForm.get();
        Repository.get().updateProduct(id, product);

        URI path = Paths.products.view(id);
        return Response.seeOther(path).build();
    }

}
