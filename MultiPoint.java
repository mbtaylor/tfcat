
import java.util.function.Consumer;
import org.json.JSONObject;

public class MultiPoint extends TfcatObject {

    private final Position[] positions_;

    public MultiPoint( JSONObject json, Bbox bbox, Position[] positions ) {
        super( json, "MultiPoint", bbox );
        positions_ = positions;
    }

    public Position[] getPositions() {
        return positions_;
    }

    public static MultiPoint createTfcat( Consumer<Report> reporter,
                                          JSONObject json, Bbox bbox ) {
        Position[] positions =
            new JsonTool( reporter )
           .asPositionArray( json.opt( "coordinates" ),
                             "MultiPoint coordinates", true );
        return positions == null ? null
                                 : new MultiPoint( json, bbox, positions );
    }
}
