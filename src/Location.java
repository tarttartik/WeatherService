import java.awt.geom.Point2D;

public class Location extends Point2D.Double {
    private final String Name;

    public Location(String name, double x, double y){
        super(x, y);
        Name = name;
    }

    public String GetName() {
        return Name;
    }
}
