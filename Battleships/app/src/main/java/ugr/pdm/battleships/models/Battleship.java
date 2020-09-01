package ugr.pdm.battleships.models;

public class Battleship {

    private int mSectionsLeft;
    private int mSize;
    private String mName;


    /**
     * Constructor
     *
     * @param size tamaño del barco
     * @param name nombre / categoría del barco
     */
    public Battleship(int size, String name) {
        mSectionsLeft = size;
        mSize = size;
        mName = name;
    }

    /**
     * @return nombre del barco
     */
    public String getName() {
        return mName;
    }

    /**
     * @return tamaño del barco
     */
    public int getSize() {
        return mSize;
    }

    /**
     * Elimina una sección del barco
     */
    public void addDamage() {
        if (mSectionsLeft - 1 >= 0)
            mSectionsLeft--;
    }

    /**
     * @return true si todas las secciones del barco han sido destruidas
     */
    public boolean isDestroyed() {
        return mSectionsLeft <= 0;
    }
}
