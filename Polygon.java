
import java.util.function.Consumer;
import org.json.JSONObject;

public class Polygon extends TfcatObject {

    private final LinearRing[] rings_;

    public Polygon( JSONObject json, Bbox bbox, LinearRing[] rings ) {
        super( json, "Polygon", bbox );
        rings_ = rings;
    }

    public LinearRing[] getLinearRings() {
        return rings_;
    }

    public static Polygon createTfcat( Consumer<Report> reporter,
                                       JSONObject json, Bbox bbox ) {
        Position[][] parrays =
            new JsonTool( reporter )
           .asPositionArrayArray( json.opt( "coordinates" ),
                                  "Polygon coordinates", true );
        if ( parrays == null ) {
            return null;
        }
        int nr = parrays.length;
        LinearRing[] rings = new LinearRing[ nr ];
        for ( int ir = 0; ir < nr; ir++ ) {
            LinearRing ring =
                LinearRing.createInstance( reporter, parrays[ ir ],
                                           "Polygon[" + ir + "]" );
            if ( ring == null ) {
                return null;
            }
            rings[ ir ] = ring;
        }
        return new Polygon( json, bbox, rings );
    }
}
