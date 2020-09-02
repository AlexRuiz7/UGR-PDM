package ugr.pdm.battleships.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Clase que repesenta un usuario
 */
@IgnoreExtraProperties
public class Friend implements Serializable {

    /**
     * Atributos
     **/
    private String personName;
    private String personEmail;
    private String personId;
    private String personPhoto;

    /**
     * Constructor vacío requerido por Firebase
     **/
    public Friend() { }

    /**
     * Constructor con parámetros
     *
     * @param personName nombre de la usuario
     * @param personEmail email del usuario
     * @param personId id del usuario
     * @param personPhoto url de la imagen de perfil del usuario
     */
    public Friend(String personName, String personEmail, String personId, String personPhoto) {
        this.personName = personName;
        this.personEmail = personEmail;
        this.personId = personId;
        this.personPhoto = personPhoto;
    }

    /**
     * Builder. Construye un nuevo objeto Friend a partir del registro de Firebase que lo almacena
     *
     * @param snapshot registro de Firebase
     * @return nuevo objeto Friend o null
     */
    public static Friend buildFromSnapshot(DataSnapshot snapshot) {
        if (snapshot.hasChildren()) {
            Friend f = snapshot.getValue(Friend.class);
            f.setPersonId(snapshot.getKey());
            return f;
        }
        return null;
    }

    /**
     * Getters
     */
    public String getPersonName() {
        return personName;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public String getPersonId() {
        return personId;
    }

    public String getPersonPhoto() {
        return personPhoto;
    }

    @Exclude
    public void setPersonId(String uid) {
        personId = uid;
    }

    @Exclude
    @Override
    public String toString() {
        return "Friend{" +
                "personName='" + personName + '\'' +
                ", personEmail='" + personEmail + '\'' +
                ", personId='" + personId + '\'' +
                ", personPhoto='" + personPhoto + '\'' +
                '}';
    }
}
