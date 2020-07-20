import java.util.List;

import processing.core.PImage;

public final class Background
{
    private final String id;
    private final List<PImage> images;
    private int imageIndex;

    public Background(String id, List<PImage> images) {
        this.id = id;
        this.images = images;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public String getId() {
        return id;
    }

    public List<PImage> getImages() {
        return images;
    }

    public PImage getCurrentImage() {
            return this.getImages().get(this.getImageIndex());
        }
    }

