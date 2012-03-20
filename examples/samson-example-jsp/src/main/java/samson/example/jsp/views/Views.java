package samson.example.jsp.views;

import java.util.Collection;

import samson.JForm;
import samson.example.jsp.model.Product;

import com.sun.jersey.api.view.Viewable;

public class Views {

    public static class Products {

        /** Tuple */
        public static class ListModel {
            public final Collection<Product> products;

            ListModel(Collection<Product> products) {
                this.products = products;
            }

            public Collection<Product> getProducts() {
                return products;
            }
        }

        public static class CreateModel {
            public final JForm<Product> productForm;

            CreateModel(JForm<Product> productForm) {
                this.productForm = productForm;
            }

            public JForm<Product> getProductForm() {
                return productForm;
            }
        }

        public static class ViewModel {
            public final Long id;
            public final Product product;

            ViewModel(Long id, Product product) {
                this.id = id;
                this.product = product;
            }

            public Long getId() {
                return id;
            }

            public Product getProduct() {
                return product;
            }
        }

        public static class EditModel {
            public final Long id;
            public final JForm<Product> productForm;

            EditModel(Long id, JForm<Product> productForm) {
                this.id = id;
                this.productForm = productForm;
            }

            public Long getId() {
                return id;
            }

            public JForm<Product> getProductForm() {
                return productForm;
            }
        }

        /** Static factory method */
        public static Viewable list(Collection<Product> products) {
            return new Viewable("/products/list", new ListModel(products));
        }

        public static Viewable create(JForm<Product> productForm) {
            return new Viewable("/products/create", new CreateModel(productForm));
        }

        public static Viewable view(Long id, Product product) {
            return new Viewable("/products/view", new ViewModel(id, product));
        }

        public static Viewable edit(Long id, JForm<Product> productForm) {
            return new Viewable("/products/edit", new EditModel(id, productForm));
        }

    }

}
