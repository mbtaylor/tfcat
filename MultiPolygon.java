
import java.util.function.Consumer;
import org.json.JSONObject;

public class MultiPolygon extends TfcatObject {

    private final LinearRing[][] ringArrays_;

    public MultiPolygon( JSONObject json, Bbox bbox,
                         LinearRing[][] ringArrays ) {
        super( json, "MultiPolygon", bbox );
    }

    public LinearRing[] getLinearRingArrays() {
        return ringArrays_;
    }

    public static MultiPolygon createTfcat( Consumer<Report> reporter,
                                            JSONObject json, Bbox bbox ) {
        Position[][][] parrays =
            new JsonTool( reporter )
           .asPositionArrayArrayArray( json.opt( "coordinates" ),
                                       "MultiPolygon coordinates", true );
        if ( parrays == null ) {
            return null;
        }
        int np = parrays.length;
  return null;
    }
}
