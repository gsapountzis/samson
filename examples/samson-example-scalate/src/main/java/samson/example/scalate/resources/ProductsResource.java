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

import samson.JForm;
import samson.JFormProvider;
import samson.example.scalate.domain.Product;

import com.sun.jersey.api.view.Viewable;

@Path("/products")
public class ProductsResource {
    public Collection<Product> products;
    public Long id;
    public Product product;
    public JForm<Product> productForm;

    @Context
    private JFormProvider jForm;

    @GET
    public Response list() {
        this.products = Repository.get().getProducts();
        Viewable view = new Viewable("list", this);
        return Response.ok(view).build();
    }

    @Path("new")
    @GET
    public Response create() {
        this.productForm = jForm.wrap(Product.class);
        Viewable view = new Viewable("create", this);
        return Response.ok(view).build();
    }

    /**
     * Create product, shows usage as a resource method parameter.
     */
    @POST
    public Response save(@FormParam("product") JForm<Product> productForm) {

        if (productForm.hasErrors()) {
            this.productForm = productForm;
            Viewable view = new Viewable("create", this);
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

        this.id = id;
        this.product = product;
        Viewable view = new Viewable("view", this);
        return Response.ok(view).build();
    }

    @Path("{id}/edit")
    @GET
    public Response edit(@PathParam("id") Long id) {
        Product product = Repository.get().findProduct(id);
        if (product == null) {
            return Response.status(NOT_FOUND).build();
        }

        this.id = id;
        this.productForm = jForm.wrap(Product.class, product);
        Viewable view = new Viewable("edit", this);
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
            this.id = id;
            this.productForm = productForm;
            Viewable view = new Viewable("edit", this);
            return Response.status(BAD_REQUEST).entity(view).build();
        }

        Product product = productForm.get();
        Repository.get().updateProduct(id, product);

        URI path = Paths.products.view(id);
        return Response.seeOther(path).build();
    }

}
