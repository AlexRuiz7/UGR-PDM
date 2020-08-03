package ugr.pdm.droidshop.utils;


/**
 * Clase que representa el estado de la applicación. El único elemento de esta clase (singleton)
 * contiene la información que desea mantener a lo largo de la aplicación y que podría querer
 * accederse desde cualquier sitio, como configuraciones de usuario (en memoria) o la información
 * del usuario registrado
 */
public class State  {

    /* Atributos privados */
    private boolean isLogged;
    private boolean isDarkMode;
    /* Event Listener propio para permitir reaccionar a cambios de estado */
    private OnDarkModeChangeListener themeListener;
    private OnAuthEventListener authListener;

    /* Instancia única de la clase */
    private static State instance = null;

    /* Constructor */
    private State () {
        isLogged = isDarkMode = false;
        themeListener = null;
        authListener = null;
    }

    /* Devuelve la instancia única de la clase */
    public static State getInstance() {
        if (instance == null) {
            instance = new State();
        }

        return instance;
    }

    /***************************************************/

    /* Devuelve true si el tema oscuro está activado, false en caso contrario */
    public boolean isDarkMode() {
        return isDarkMode;
    }

    /* Permite cambiar el tema de la aplicación */
    public void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;

        if (themeListener != null)
            themeListener.onModeChanged();
    }

    /* Permite agregar un Event Listener personalizado */
    public void setOnDarkModeChangeListener (OnDarkModeChangeListener listener) {
        this.themeListener = listener;
    }

    /* Interfaz que implementa el Event Listener */
    public interface OnDarkModeChangeListener {
        void onModeChanged ();
    }

    /****************************************/

    /* Permite cambiar el estado de registro del usuario */
    public void setLogged(boolean logged) {
        isLogged = logged;

        if (authListener != null)
            authListener.onIsLoggedChange();
    }

    /* Permite agregar un Event Listener personalizado */
    public void setOnAuthChangeListener (OnAuthEventListener listener) {
        this.authListener = listener;
    }

    /* Devuelve el estado de registro del usuario */
    public boolean isLogged() {
        return isLogged;
    }

    public interface OnAuthEventListener {
        void onIsLoggedChange ();
    }

}
