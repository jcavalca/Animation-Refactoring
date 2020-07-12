import java.util.List;
import java.util.Optional;

public final class Point
{
    private final int X;
    private final int Y;

    public Point(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    public String toString() {
        return "(" + X + "," + Y + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Point && ((Point)other).X == this.X
                && ((Point)other).Y == this.Y;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + X;
        result = result * 31 + Y;
        return result;
    }

    public boolean adjacent(Point p2) {
        return (this.X == p2.X && Math.abs(this.Y - p2.Y) == 1) || (this.Y == p2.Y
                && Math.abs(this.X - p2.X) == 1);
    }

    private int distanceSquared(Point p2) {
        int deltaX = this.X - p2.X;
        int deltaY = this.Y - p2.Y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public Optional<Entity> nearestEntity( //// Gotta solve
                                                  List<Entity> entities) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = nearest.getPosition().distanceSquared(this);

            for (Entity other : entities) {
                int otherDistance = other.getPosition().distanceSquared(this);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }
}
