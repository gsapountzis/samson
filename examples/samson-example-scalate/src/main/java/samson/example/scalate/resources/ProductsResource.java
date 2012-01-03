package samson.example.scalate.resources;

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
import javax.ws.rs.core.UriBuilder;

import samson.JForm;
import samson.JFormProvider;
import samson.example.scalate.model.Product;
import samson.example.scalate.model.Repository;

import com.sun.jersey.api.view.Viewable;

@Path("/products")
public class ProductsResource {

    @Context
    private JFormProvider jForm;

    @GET
    public Response list() {
        Collection<Product> products = Repository.get().getProducts();
        return Response.ok(views.list(products)).build();
    }

    @Path("new")
    @GET
    public Response create() {
        JForm<Product> productForm = jForm.wrap(Product.class);
        return Response.ok(views.create(productForm)).build();
    }

    /**
     * Create product, shows usage as a resource method parameter.
     */
    @POST
    public Response save(@FormParam("") JForm<Product> productForm) {

        if (productForm.hasErrors()) {
            return Response.status(BAD_REQUEST).entity(views.create(productForm)).build();
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

        return Response.ok(views.view(id, product)).build();
    }

    @Path("{id}/edit")
    @GET
    public Response edit(@PathParam("id") Long id) {
        Product product = Repository.get().findProduct(id);
        if (product == null) {
            return Response.status(NOT_FOUND).build();
        }

        JForm<Product> productForm = jForm.wrap(Product.class, product);
        return Response.ok(views.edit(id, productForm)).build();
    }

    /**
     * Update product, shows usage within the resource method body.
     */
    @Path("{id}")
    @POST
    public Response update(@PathParam("id") Long id) {

        JForm<Product> productForm = jForm.form().bind(Product.class);

        if (productForm.hasErrors()) {
            return Response.status(BAD_REQUEST).entity(views.edit(id, productForm)).build();
        }

        Product product = productForm.get();
        Repository.get().updateProduct(id, product);

        return Response.seeOther(Paths.view(id)).build();
    }

    // -- Boilerplate

    public static class Paths  {

        public static URI list() {
            return UriBuilder.fromResource(ProductsResource.class).build();
        }

        public static URI view(Long id) {
            return UriBuilder.fromResource(ProductsResource.class).path(ProductsResource.class, "view").build(id);
        }

    }

    public static class Views {

        private final ProductsResource resource;

        public Views(ProductsResource resource) {
            this.resource = resource;
        }

        public Viewable list(Collection<Product> products) {
            resource.model = new Model(products, null, null, null);
            return new Viewable("list", resource);
        }

        public Viewable create(JForm<Product> productForm) {
            resource.model = new Model(null, productForm, null, null);
            return new Viewable("create", resource);
        }

        public Viewable view(Long id, Product product) {
            resource.model = new Model(null, null, id, product);
            return new Viewable("view", resource);
        }

        public Viewable edit(Long id, JForm<Product> productForm) {
            resource.model = new Model(null, productForm, id, null);
            return new Viewable("edit", resource);
        }

    }

    /** Tuple */
    public static class Model {
        public final Collection<Product> products;
        public final JForm<Product> productForm;
        public final Long id;
        public final Product product;

        Model(Collection<Product> products, JForm<Product> productForm, Long id, Product product) {
            this.products = products;
            this.productForm = productForm;
            this.id = id;
            this.product = product;
        }
    }

    private final Views views = new Views(this);

    public Model model;

}
