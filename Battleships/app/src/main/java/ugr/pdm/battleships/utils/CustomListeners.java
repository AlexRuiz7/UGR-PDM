package ugr.pdm.battleships.utils;

/**
 * Interfaz necesaria para poder actualizar la interfaz de la pantalla de amigos cuando se elimina
 * un elemento, ya que el onClick listener se encuentra en el ViewHolder en sí y no en la clase
 * principal de la actividad.
 */
public class CustomListeners {
    /* Event Listener propio para permitir reaccionar a cambios de estado */
    private OnDataChangeListener listener;

    /* Instancia única de la clase */
    private static CustomListeners instance = null;

    /* Constructor */
    private CustomListeners () {
        listener = null;
    }

    /* Devuelve la instancia única de la clase */
    public static CustomListeners getInstance() {
        if (instance == null) {
            instance = new CustomListeners();
        }

        return instance;
    }

    /* Permite cambiar el tema de la aplicación */
    public void notifyDataSetChanged() {

        if (listener != null)
            listener.onDataChanged();
    }

    /* Permite agregar un Event Listener personalizado */
    public void setOnDataChangeListener (OnDataChangeListener listener) {
        this.listener = listener;
    }

    /* Interfaz que implementa el Event Listener */
    public interface OnDataChangeListener {
        void onDataChanged ();
    }
}
