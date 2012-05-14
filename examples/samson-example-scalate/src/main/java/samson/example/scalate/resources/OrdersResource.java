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

import samson.example.scalate.model.Customer;
import samson.example.scalate.model.Order;
import samson.example.scalate.model.OrderForm;
import samson.example.scalate.model.Product;
import samson.example.scalate.model.Repository;
import samson.example.scalate.views.Views;
import samson.form.FormProvider;
import samson.form.SamsonForm;

@Path("/orders")
public class OrdersResource {

    private final FormProvider jForm;
    private final Repository repository;

    public OrdersResource(@Context FormProvider jForm) {
        this.jForm = jForm;
        this.repository = Repository.get();
    }

    @GET
    public Response list() {
        Collection<Order> orders = repository.getOrders();
        return Response.ok(Views.Orders.list(orders)).build();
    }

    @Path("{id}")
    @GET
    public Response view(@PathParam("id") Long id) {
        Order order = repository.findOrder(id);
        if (order == null) {
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(Views.Orders.view(id, order)).build();
    }

    @Path("{id}/edit")
    @GET
    public Response edit(@PathParam("id") Long id) {
        Order order = repository.findOrder(id);
        if (order == null) {
            return Response.status(NOT_FOUND).build();
        }

        SamsonForm<Order> orderForm = jForm.wrap(Order.class, order);
        Map<String, String> customerOptions = getCustomerOptions();
        Map<String, String> productOptions = getProductOptions();

        return Response
                .ok(Views.Orders.edit(id, orderForm, customerOptions, productOptions))
                .build();
    }

    @Path("{id}")
    @POST
    public Response update(@PathParam("id") Long id, SamsonForm<Order> orderFormParam) {
        OrderForm orderForm = new OrderForm(orderFormParam).validate();

        if (orderForm.hasErrors()) {
            Map<String, String> customerOptions = getCustomerOptions();
            Map<String, String> productOptions = getProductOptions();

            return Response
                    .status(BAD_REQUEST)
                    .entity(Views.Orders.edit(id, orderForm, customerOptions, productOptions))
                    .build();
        }

        Order order = orderForm.get();
        repository.updateOrder(id, order);

        return Response.seeOther(Paths.view(id)).build();
    }

    // -- Option values (should be cached either at the resources or repository level)

    private Map<String, String> getCustomerOptions() {
        final Map<String, String> unsorted = new HashMap<String, String>();
        for (Customer c : repository.getCustomers()) {
            unsorted.put(Long.toString(c.id), c.name);
        }

        return sortByValue(unsorted);
    }

    private Map<String, String> getProductOptions() {
        final Map<String, String> unsorted = new HashMap<String, String>();
        for (Product p : repository.getProducts()) {
            unsorted.put(Long.toString(p.id), p.name);
        }

        return sortByValue(unsorted);
    }

    private Map<String, String> sortByValue(final Map<String, String> unsorted) {

        Map<String, String> options = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return unsorted.get(o1).compareTo(unsorted.get(o2));
            }
        });
        options.putAll(unsorted);
        return options;
    }

    // -- Reverse routing

    public static class Paths  {

        public static URI list() {
            return UriBuilder.fromResource(OrdersResource.class).build();
        }

        public static URI view(Long id) {
            return UriBuilder.fromResource(OrdersResource.class).path(OrdersResource.class, "view").build(id);
        }

    }

}
