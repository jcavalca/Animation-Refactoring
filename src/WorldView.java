import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;

public final class WorldView
{
    private final PApplet SCREEN;
    private final WorldModel WORLD;
    private final int TILEWIDTH;
    private final int TILEHEIGHT;
    private final Viewport VIEWPORT;

    public WorldView(
            int numRows,
            int numCols,
            PApplet SCREEN,
            WorldModel WORLD,
            int TILEWIDTH,
            int TILEHEIGHT)
    {
        this.SCREEN = SCREEN;
        this.WORLD = WORLD;
        this.TILEWIDTH = TILEWIDTH;
        this.TILEHEIGHT = TILEHEIGHT;
        this.VIEWPORT = new Viewport(numRows, numCols);
    }

    private int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }

    public void shiftView(int colDelta, int rowDelta) {
        int newCol = clamp(this.VIEWPORT.getCol() + colDelta, 0,
                this.WORLD.getNumCols() - this.VIEWPORT.getNUMCOLS());
        int newRow = clamp(this.VIEWPORT.getRow() + rowDelta, 0,
                this.WORLD.getNUMROWS() - this.VIEWPORT.getNUMROWS());

        this.VIEWPORT.shift(newCol, newRow);
    }

    private void drawBackground() {
        for (int row = 0; row < this.VIEWPORT.getNUMROWS(); row++) {
            for (int col = 0; col < this.VIEWPORT.getNUMCOLS(); col++) {
                Point worldPoint = this.VIEWPORT.viewportToWorld(col, row);
                Optional<PImage> image =
                        this.WORLD.getBackgroundImage(worldPoint);
                if (image.isPresent()) {
                    this.SCREEN.image(image.get(), col * this.TILEWIDTH,
                            row * this.TILEHEIGHT);
                }
            }
        }
    }

    private void drawEntities() {
        for (Entity entity : this.WORLD.getENTITIES()) {
            Point pos = entity.getPosition();

            if (this.VIEWPORT.contains(pos)) {
                Point viewPoint = this.VIEWPORT.worldToViewport(pos.x, pos.y);
                this.SCREEN.image(entity.getCurrentImage(),
                        viewPoint.x * this.TILEWIDTH,
                        viewPoint.y * this.TILEHEIGHT);
            }
        }
    }

    public void drawViewport() {
        this.drawBackground();
        this.drawEntities();
    }
}
