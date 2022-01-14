
import java.util.function.Consumer;
import org.json.JSONObject;

public class LineString extends TfcatObject {

    private final Position[] positions_;

    public LineString( JSONObject json, Bbox bbox, Position[] positions ) {
        super( json, "LineString", bbox );
        positions_ = positions;
    }

    public Position[] getPositions() {
        return positions_;
    }

    public static LineString createTfcat( Consumer<Report> reporter,
                                          JSONObject json, Bbox bbox ) {
        Position[] positions =
            new JsonTool( reporter )
           .asPositionArray( json.opt( "coordinates" ),
                             "LineString coordinates", true );
        if ( positions == null ) {
            return null;
        }
        else if ( positions.length < 2 ) {
            reporter.accept( new Report( Level.ERROR, "LSTF",
                                         "LineString: too few points"
                                       + " (" + positions.length + "<2)" ) );
            return null;
        }
        else {
            return new LineString( json, bbox, positions );
        }
    }
}
