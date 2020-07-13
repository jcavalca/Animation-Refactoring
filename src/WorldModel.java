import processing.core.PImage;

import java.util.*;

public final class WorldModel
{
    private final int NUMROWS;
    private final int NUMCOLS;
    private final Background BACKGROUND[][];
    private final Entity OCCUPANCY[][];
    private final Set<Entity> ENTITIES;

    public WorldModel(int NUMROWS, int numCols, Background defaultBackground) {
        this.NUMROWS = NUMROWS;
        this.NUMCOLS = numCols;
        this.BACKGROUND = new Background[NUMROWS][numCols];
        this.OCCUPANCY = new Entity[NUMROWS][numCols];
        this.ENTITIES = new HashSet<>();

        for (int row = 0; row < NUMROWS; row++) {
            Arrays.fill(this.BACKGROUND[row], defaultBackground);
        }
    }

    public Optional<Point> findOpenAround(Point pos) {
        for (int dy = -Functions.ORE_REACH; dy <= Functions.ORE_REACH; dy++) {
            for (int dx = -Functions.ORE_REACH; dx <= Functions.ORE_REACH; dx++) {
                Point newPt = new Point(pos.x + dx, pos.y + dy);
                if (this.withinBounds(newPt) && !this.isOccupied(newPt)) {
                    return Optional.of(newPt);
                }
            }
        }

        return Optional.empty();
    }

    public void tryAddEntity(Entity entity) {
        if (this.isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }

    private boolean withinBounds(Point pos) {
        return pos.y >= 0 && pos.y < this.NUMROWS && pos.x >= 0
                && pos.x < this.NUMCOLS;
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    public Optional<Entity> findNearest(
             Point pos, EntityKind kind)
    {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : this.ENTITIES) {
            if (entity.getKIND() == kind) {
                ofType.add(entity);
            }
        }

        return pos.nearestEntity(ofType);
    }

    /*
      Assumes that there is no entity currently occupying the
      intended destination cell.
   */
    public void addEntity( Entity entity) {
        if (this.withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.ENTITIES.add(entity);
        }
    }
    public void moveEntity(Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            this.removeEntityAt(pos);
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public void removeEntity(Entity entity) {
        this.removeEntityAt(entity.getPosition());
    }

    private void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.ENTITIES.remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }

    public Optional<PImage> getBackgroundImage(
           Point pos)
    {
        if (this.withinBounds(pos)) {
            return Optional.of(this.getBackgroundCell(pos).getCurrentImage());
        }
        else {
            return Optional.empty();
        }
    }

    public void setBackground(Point pos, Background background)
    {
        if (this.withinBounds(pos)) {
            this.setBackgroundCell(pos, background);
        }
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        }
        else {
            return Optional.empty();
        }
    }

    private Entity getOccupancyCell(Point pos) {
        return this.OCCUPANCY[pos.y][pos.x];
    }

    private void setOccupancyCell(
            Point pos, Entity entity)
    {
        this.OCCUPANCY[pos.y][pos.x] = entity;
    }

    private Background getBackgroundCell(Point pos) {
        return this.BACKGROUND[pos.y][pos.x];
    }

    private void setBackgroundCell(
            Point pos, Background background)
    {
        this.BACKGROUND[pos.y][pos.x] = background;
    }

    public int getNUMROWS() {
        return NUMROWS;
    }

    public int getNumCols() {
        return NUMCOLS;
    }

    public Set<Entity> getENTITIES() {
        return ENTITIES;
    }
}

