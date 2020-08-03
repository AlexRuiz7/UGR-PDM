package ugr.pdm.droidshop.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.NumberFormat;
import java.util.Currency;

@IgnoreExtraProperties
public class CartItem {

    private Product product;
    private int quantity;
    private double subtotal;


    /**
     * Constructor vacío requqerido por Firebase
     */
    public CartItem() { }


    /**
     * Constructor
     * @param aProduct
     * @param aQuantity
     */
    public CartItem(Product aProduct, int aQuantity) {
        product = aProduct;
        quantity = aQuantity;
        subtotal = product.getPrice() * quantity;
        subtotal = Math.round(subtotal * 100.0) / 100.0;
    }


    /**
     * Getters
     *
     */
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    @Exclude
    public String getSubtotalCurrency () {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setCurrency(Currency.getInstance("EUR"));
        return format.format(subtotal);
    };
}
