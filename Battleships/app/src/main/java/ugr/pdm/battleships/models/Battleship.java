package ugr.pdm.battleships.models;

public class Battleship {

    private int sectionsLeft;
    private int size;
    private String name;


    /**
     * Constructor
     *
     * @param size tamaño del barco
     * @param name nombre / categoría del barco
     */
    public Battleship(int size, String name) {
        sectionsLeft = size;
        this.size = size;
        this.name = name;
    }

    /**
     * @return nombre del barco
     */
    public String getName() {
        return name;
    }

    /**
     * @return tamaño del barco
     */
    public int getSize() {
        return size;
    }

    /**
     * Elimina una sección del barco
     */
    public void addDamage() {
        if (sectionsLeft - 1 >= 0)
            sectionsLeft--;
    }

    /**
     * @return true si todas las secciones del barco han sido destruidas
     */
    public boolean isDestroyed() {
        return sectionsLeft <= 0;
    }

    /**
     * Restaura la "vida" del barco
     */
    public void revive() {
        sectionsLeft = size;
    }
}
