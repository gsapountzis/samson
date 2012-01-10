package samson.example.scalate.resources;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import samson.JForm;
import samson.JFormProvider;
import samson.example.scalate.model.Product;
import samson.example.scalate.model.Repository;
import samson.example.scalate.views.Views;

@Path("/products")
public class ProductsResource {

    @Context
    private JFormProvider jForm;

    @GET
    public Response list() {
        Collection<Product> products = Repository.get().getProducts();
        return Response.ok(Views.Products.list(products)).build();
    }

    @Path("new")
    @GET
    public Response create() {
        JForm<Product> productForm = jForm.wrap(Product.class);
        return Response.ok(Views.Products.create(productForm)).build();
    }

    /**
     * Create product, shows usage as a resource method parameter.
     */
    @POST
    public Response save(JForm<Product> productForm) {

        if (productForm.hasErrors()) {
            return Response.status(BAD_REQUEST).entity(Views.Products.create(productForm)).build();
        }

        Product product = productForm.get();
        Long id = Repository.get().createProduct(product);

        return Response.seeOther(Paths.view(id)).build();
    }

    @Path("{id}")
    @GET
    public Response view(@PathParam("id") Long id) {
        Product product = Repository.get().findProduct(id);
        if (product == null) {
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(Views.Products.view(id, product)).build();
    }

    @Path("{id}/edit")
    @GET
    public Response edit(@PathParam("id") Long id) {
        Product product = Repository.get().findProduct(id);
        if (product == null) {
            return Response.status(NOT_FOUND).build();
        }

        JForm<Product> productForm = jForm.wrap(Product.class, product);
        return Response.ok(Views.Products.edit(id, productForm)).build();
    }

    /**
     * Update product, shows usage within the resource method body.
     */
    @Path("{id}")
    @POST
    public Response update(@PathParam("id") Long id) {

        JForm<Product> productForm = jForm.form().bind(Product.class);

        if (productForm.hasErrors()) {
            return Response.status(BAD_REQUEST).entity(Views.Products.edit(id, productForm)).build();
        }

        Product product = productForm.get();
        Repository.get().updateProduct(id, product);

        return Response.seeOther(Paths.view(id)).build();
    }

    // -- Reverse routing

    public static class Paths  {

        public static URI list() {
            return UriBuilder.fromResource(ProductsResource.class).build();
        }

        public static URI view(Long id) {
            return UriBuilder.fromResource(ProductsResource.class).path(ProductsResource.class, "view").build(id);
        }

    }

}
