package ugr.pdm.droidshop.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Clase que repesenta un comentario de un usuario sobre un producto.
 *
 *  - UUID representa el ID de usuario que reliza el comentario.
 *  - Username repesenta el nombre del usuario que realiza el comentario. Se podría obtener haciendo
 *    una consulta sobre UUID pero priorizo la simplicidad
 *  - text es el texto del comentario realizado
 *  - Date es el timestamp del comentario.
 */
@IgnoreExtraProperties
public class Review {

//    public static String SERIALIZABLE_KEY = "REVIEW";

    /** Atributos **/
    private String uuid;
    private String username;
    private String text;
    private String timestamp;

    @Exclude
    private String productID;
    @Exclude
    private String reviewID;

    public Review(String UUID, String username, String text, String timestamp) {
        this.uuid = UUID;
        this.username = username;
        this.text = text;
        this.timestamp = timestamp;
    }

    /** Constructor vacío requerido por Firebase **/
    public Review() {   }


    /**
     * Getters
     *
     */
    public String getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Exclude
    public String getProductID() {
        return productID;
    }
    @Exclude
    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }
}
