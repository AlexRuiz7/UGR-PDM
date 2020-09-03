package ugr.pdm.battleships.utils;

import ugr.pdm.battleships.models.Battle;
import ugr.pdm.battleships.models.Friend;

/**
 * Interfaz necesaria para poder actualizar la interfaz de la pantalla de amigos cuando se elimina
 * un elemento, ya que el onClick listener se encuentra en el ViewHolder en sí y no en la clase
 * principal de la actividad.
 */
public class CustomListeners {
    /* Event Listener propio para permitir reaccionar a cambios de estado */
    private OnDataChangeListener listener;
    private OnFriendClickedListener friendListener;
    private OnBattleAcceptedListener battleListener;

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

    /* Permite agregar un Event Listener personalizado */
    public void setFriendClickedListener (OnFriendClickedListener listener) {
        this.friendListener = listener;
    }

    //
    public void onFriendTouched(Friend f) {
        if (friendListener != null)
            friendListener.onFriendClicked(f);
    }

    /* Interfaz */
    public interface OnFriendClickedListener {
        void onFriendClicked(Friend f);
    }


    public void setBattleAcceptedListener(OnBattleAcceptedListener listener) {
        battleListener = listener;
    }

    public void onBattleAccepted(Battle b) {
        if (battleListener != null) {
            battleListener.onBattleAccepted(b);
        }
    }

    public interface OnBattleAcceptedListener {
        void onBattleAccepted(Battle b);
    }
}
