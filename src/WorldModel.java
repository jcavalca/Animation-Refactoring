import java.util.*;

public final class WorldModel
{
    public int numRows;
    public int numCols;
    public Background background[][];
    public Entity occupancy[][];
    public Set<Entity> entities;

    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.entities = new HashSet<>();

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
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
        if (this.isOccupied(entity.position)) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        Functions.addEntity(this, entity);
    }

    public boolean withinBounds(Point pos) {
        return pos.y >= 0 && pos.y < this.numRows && pos.x >= 0
                && pos.x < this.numCols;
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && Functions.getOccupancyCell(this, pos) != null;
    }

    public Optional<Entity> findNearest(
             Point pos, EntityKind kind)
    {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : this.entities) {
            if (entity.kind == kind) {
                ofType.add(entity);
            }
        }

        return Functions.nearestEntity(ofType, pos);
    }
}
