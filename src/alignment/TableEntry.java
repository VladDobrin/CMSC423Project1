package alignment;
public class TableEntry {
    
    public enum Path {UP, DIAG, LEFT}
    private Path path;
    private int score;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
    
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}