import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public class Blacksmith extends Entity{

    public Blacksmith(
            String id,
            Point position,
            List<PImage> images)
    {
        super(id, position, images);
    }
}
