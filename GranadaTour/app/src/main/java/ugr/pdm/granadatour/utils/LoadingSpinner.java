package ugr.pdm.granadatour.utils;

import android.util.Log;

public class LoadingSpinner {
    private OnLoadListener listener;

    /* Instancia única de la clase */
    private static LoadingSpinner instance = null;

    /* Constructor */
    private LoadingSpinner() {
        listener = null;
    }

    /* Devuelve la instancia única de la clase */
    public static LoadingSpinner getInstance() {
        if (instance == null) {
            instance = new LoadingSpinner();
        }

        return instance;
    }


    // Loading Fragments
    public void setLoading(boolean isLoading) {
        if (listener != null) {
            if (isLoading) {
                listener.onLoadStart();
                Log.e("SPINNER", "Cargando contenido");
            }
            else {
                listener.onLoadEnd();
                Log.e("SPINNER", "Contenido cargado");
            }
        }
    }

    // MainActivity
    public void setListener(OnLoadListener listener) {
        this.listener = listener;
    }

    // MainActivity
    public interface OnLoadListener {
        void onLoadStart();

        void onLoadEnd();
    }
}
