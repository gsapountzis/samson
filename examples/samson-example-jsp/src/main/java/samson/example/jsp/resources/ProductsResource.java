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
import javax.ws.rs.core.UriBuilder;

import samson.JForm;
import samson.JFormProvider;
import samson.example.jsp.model.Product;
import samson.example.jsp.model.Repository;

import com.sun.jersey.api.view.Viewable;

@Path("/products")
public class ProductsResource {

    @Context
    private JFormProvider jForm;

    @GET
    public Response list() {
        Collection<Product> products = Repository.get().getProducts();
        return Response.ok(Views.list(products)).build();
    }

    @Path("new")
    @GET
    public Response create() {
        JForm<Product> productForm = jForm.wrap(Product.class);
        return Response.ok(Views.create(productForm)).build();
    }

    /**
     * Create product, shows usage as a resource method parameter.
     */
    @POST
    public Response save(@FormParam("product") JForm<Product> productForm) {

        if (productForm.hasErrors()) {
            return Response.status(BAD_REQUEST).entity(Views.create(productForm)).build();
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

        return Response.ok(Views.view(id, product)).build();
    }

    @Path("{id}/edit")
    @GET
    public Response edit(@PathParam("id") Long id) {
        Product product = Repository.get().findProduct(id);
        if (product == null) {
            return Response.status(NOT_FOUND).build();
        }

        JForm<Product> productForm = jForm.wrap(Product.class, product);
        return Response.ok(Views.edit(id, productForm)).build();
    }

    /**
     * Update product, shows usage within the resource method body.
     */
    @Path("{id}")
    @POST
    public Response update(@PathParam("id") Long id) {

        JForm<Product> productForm = jForm.bind(Product.class).form("product");

        if (productForm.hasErrors()) {
            return Response.status(BAD_REQUEST).entity(Views.edit(id, productForm)).build();
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

        public static Viewable list(Collection<Product> products) {
            Model model = new Model(products, null, null, null);
            return new Viewable("/WEB-INF/views/products/list", model);
        }

        public static Viewable create(JForm<Product> productForm) {
            Model model = new Model(null, productForm, null, null);
            return new Viewable("/WEB-INF/views/products/create", model);
        }

        public static Viewable view(Long id, Product product) {
            Model model = new Model(null, null, id, product);
            return new Viewable("/WEB-INF/views/products/view", model);
        }

        public static Viewable edit(Long id, JForm<Product> productForm) {
            Model model = new Model(null, productForm, id, null);
            return new Viewable("/WEB-INF/views/products/edit", model);
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

        public Collection<Product> getProducts() {
            return products;
        }

        public JForm<Product> getProductForm() {
            return productForm;
        }

        public Long getId() {
            return id;
        }

        public Product getProduct() {
            return product;
        }
    }

}
