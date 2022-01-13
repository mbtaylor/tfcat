
import java.util.function.Consumer;
import org.json.JSONObject;

public class Point extends TfcatObject {

    private final Position pos_;

    public Point( JSONObject json, Bbox bbox, Position pos ) {
        super( json, "Point", bbox );
        pos_ = pos;
    }

    public Position getPosition() {
        return pos_;
    }

    public static Point createTfcat( Consumer<Report> reporter, JSONObject json,
                                     Bbox bbox ) {
        Position pos = new JsonTool( reporter )
                      .asPosition( json.opt( "coordinates" ),
                                   "Point coordinates", true );
        return pos == null ? null : new Point( json, bbox, pos );
    }
}
