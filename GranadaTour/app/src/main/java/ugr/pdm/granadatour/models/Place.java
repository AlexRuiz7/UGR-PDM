package ugr.pdm.granadatour.models;

public class Place extends Item {

    private String coords;

    /**
     * Empty constructor required by firebase getValue(<Class>)
     */
    public Place () {}


    /**
     * Getters
     */
    public String getCoords() {
        return coords;
    }

}
