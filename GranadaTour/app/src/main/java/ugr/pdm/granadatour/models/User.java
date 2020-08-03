package ugr.pdm.granadatour.models;

public class User  {

    private String title;
    private String subtitle;
    private String info;
    private String imageResource;

    /**
     * Empty constructor required by firebase getValue(<Class>)
     */
    public User () {}


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

    public String getImageResource() {
        return imageResource;
    }
}
