package ugr.pdm.droidshop.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

@IgnoreExtraProperties
public class Product implements Serializable {

    public static String SERIALIZABLE_KEY = "PRODUCT";

    /** Atributos **/
    private String title;
    private String info;
    private double price;
    private String subtitle;
    private String image;
    private String key;
    @Exclude
    private ArrayList<Review> reviews;

    public Product(String title, String subtitle, String info, double price) {
        this.title = title;
        this.subtitle = subtitle;
        this.info = info;
        this.price = price;
    }

    /** Constructor vacío requerido por Firebase **/
    public Product() {}


    /**
     * Getters
     *
     */
    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public double getPrice() {
        return price;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImage() {
        return image;
    }

    public String getKey() {
        return key;
    }


    /**
     * Comprueba si image es una URL de tipo Storage valida. Realizando esa comporbacion se evitan
     * errores de tipo null pointer al leer la image con Glide
     *
     * @return
     */
    @Exclude
    public boolean isValidURL() {
        if (image != null)
            return image.split(":")[0].equals("gs");
        else
            return false;
    }

    /**
     * Devuelve una referencia de tipo StorageReference de la imagen del producto alojada en Firebase
     *
     * @return StorageReference a la imagen
     */
    @Exclude
    public StorageReference getImageReference () {
        return  FirebaseStorage.getInstance().getReferenceFromUrl(image);
    }

    @Exclude
    public String getPriceCurrency () {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setCurrency(Currency.getInstance("EUR"));
        return format.format(price);
    };

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    @Exclude
    public String toString() {
        return "Product{" +
                "key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", info='" + info + '\'' +
                ", price=" + price +
                ", subtitle='" + subtitle + '\'' +
                ", image='" + image + '\'' +
                ", reviews=" + reviews +
                '}';
    }
}
