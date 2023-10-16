package byow.Core;

public class Avatar extends Build {
    String name;
    int pt;
    public Avatar(int x, int y, int w, int h, String n, int points) {
        super(x, y, w, h);
        type = "avatar";
        this.name = n;
        this.pt = points;
    }
}
