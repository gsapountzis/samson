package samson.example.scalate.resources;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URI;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import samson.JForm;
import samson.JFormProvider;
import samson.example.scalate.model.Customer;
import samson.example.scalate.model.Order;
import samson.example.scalate.model.Repository;

import com.sun.jersey.api.view.Viewable;

@Path("/orders")
public class OrdersResource {

    @Context
    private JFormProvider jForm;

    @GET
    public Response list() {
        Collection<Order> orders = Repository.get().getOrders();
        return Response.ok(views.list(orders)).build();
    }

    @Path("{id}")
    @GET
    public Response view(@PathParam("id") Long id) {
        Order order = Repository.get().findOrder(id);
        if (order == null) {
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(views.view(id, order)).build();
    }

    @Path("{id}/edit")
    @GET
    public Response edit(@PathParam("id") Long id) {
        Order order = Repository.get().findOrder(id);
        if (order == null) {
            return Response.status(NOT_FOUND).build();
        }

        JForm<Order> orderForm = jForm.wrap(Order.class, order);
        return Response.ok(views.edit(id, orderForm)).build();
    }

    @Path("{id}")
    @POST
    public Response update(@PathParam("id") Long id) {

        JForm<Order> orderForm = jForm.bind(Order.class).form("order");

        if (orderForm.hasErrors()) {
            return Response.status(BAD_REQUEST).entity(views.edit(id, orderForm)).build();
        }

        Order order = orderForm.get();
        Repository.get().updateOrder(id, order);

        return Response.seeOther(Paths.view(id)).build();
    }

    public Map<String, String> getCustomerOptions() {
        final Map<String, String> unsorted = new HashMap<String, String>();
        for (Customer c : Repository.get().getCustomers()) {
            unsorted.put(c.id.toString(), c.name);
        }

        Map<String, String> options = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return unsorted.get(o1).compareTo(unsorted.get(o2));
            }
        });
        options.putAll(unsorted);
        return options;
    }

    // -- Boilerplate

    public static class Paths  {

        public static URI list() {
            return UriBuilder.fromResource(OrdersResource.class).build();
        }

        public static URI view(Long id) {
            return UriBuilder.fromResource(OrdersResource.class).path(OrdersResource.class, "view").build(id);
        }

    }

    public static class Views {

        private final OrdersResource resource;

        public Views(OrdersResource resource) {
            this.resource = resource;
        }

        public Viewable list(Collection<Order> orders) {
            resource.model = new Model(orders, null, null, null);
            return new Viewable("list", resource);
        }

        public Viewable view(Long id, Order order) {
            resource.model = new Model(null, null, id, order);
            return new Viewable("view", resource);
        }

        public Viewable edit(Long id, JForm<Order> orderForm) {
            resource.model = new Model(null, orderForm, id, null);
            return new Viewable("edit", resource);
        }

    }

    /** Tuple */
    public static class Model {
        public final Collection<Order> orders;
        public final JForm<Order> orderForm;
        public final Long id;
        public final Order order;

        Model(Collection<Order> orders, JForm<Order> orderForm, Long id, Order order) {
            this.orders = orders;
            this.orderForm = orderForm;
            this.id = id;
            this.order = order;
        }
    }

    private final Views views = new Views(this);

    public Model model;

}
