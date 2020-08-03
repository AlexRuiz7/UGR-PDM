package ugr.pdm.granadatour.models;

public class Route extends Item {

    private String routePoints;

    /**
     * Empty constructor required by firebase getValue(<Class>)
     */
    public Route() { }


    /**
     * Getters
     */
    public String getRoutePoints() {
        return routePoints;
    }

}
