import java.util.List;

import processing.core.PImage;

public final class Background
{
    private final String ID;
    private final List<PImage> IMAGES;
    private int imageIndex;

    public Background(String ID, List<PImage> IMAGES) {
        this.ID = ID;
        this.IMAGES = IMAGES;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public String getID() {
        return ID;
    }

    public List<PImage> getIMAGES() {
        return IMAGES;
    }

    public PImage getCurrentImage() {
            return this.getIMAGES().get(this.getImageIndex());
        }
    }

