public final class Viewport
{
    private int row;
    private int col;
    private final int NUMROWS;
    private final int NUMCOLS;

    public Viewport(int NUMROWS, int NUMCOLS) {
        this.NUMROWS = NUMROWS;
        this.NUMCOLS = NUMCOLS;
    }

    public void shift(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public boolean contains(Point p) {
        return p.y >= this.row && p.y < this.row + this.NUMROWS
                && p.x >= this.col && p.x < this.col + this.NUMCOLS;
    }

    public Point viewportToWorld(int col, int row) {
        return new Point(col + this.col, row + this.row);
    }

    public Point worldToViewport(int col, int row) {
        return new Point(col - this.col, row - this.row);
    }


    // getters

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getNUMCOLS() {
        return NUMCOLS;
    }

    public int getNUMROWS() {
        return NUMROWS;
    }
}
