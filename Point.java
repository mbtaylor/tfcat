
import java.util.function.Consumer;
import org.json.JSONObject;

public class Point extends TfcatObject {

    private final Position pos_;

    public Point( JSONObject json, Bbox bbox, Position pos ) {
        super( json, "Point", bbox );
        pos_ = pos;
    }

    public static Point createTfcat( Consumer<Report> reporter, JSONObject json,
                                     Bbox bbox ) {
        double[] coords =
            TfcatParser.getNumericArray( reporter, json, "coordinates" );
        Position pos = Position.create( reporter, coords );
        return pos == null
             ? null
             : new Point( json, bbox, pos );
    }
}
