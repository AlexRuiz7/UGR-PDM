package ugr.pdm.granadatour.models;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

/**
 * Generalización de Places y Routes
 */
public abstract class Item implements Serializable {

    public static final String SERIALZABLE_KEY = "item";

    protected String title;
    protected String subtitle;
    protected String info;
    protected String image;


    /**
     * Empty constructor required by firebase getValue(<Class>)
     */
    public Item() {};


    /**
     * Getters
     *
     */
    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getInfo() {
        return info;
    }

    public String getImage() {
        return image;
    }

    /**
     *
     * @return
     */
    public boolean isValidURL() {
        return image.split(":")[0].equals("gs");
    }

    /**
     * Create a reference to a file from a Google Cloud Storage URI
     *
     * @return StorageReference a la imagen
     */
    public StorageReference getImageReference () {
        return  FirebaseStorage.getInstance().getReferenceFromUrl(image);
    }

}
