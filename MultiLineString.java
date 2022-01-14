
import java.util.function.Consumer;
import org.json.JSONObject;

public class MultiLineString extends TfcatObject {

    private final Position[][] lines_;

    public MultiLineString( JSONObject json, Bbox bbox, Position[][] lines ) {
        super( json, "MultiLineString", bbox );
        lines_ = lines;
    }

    public Position[][] getLines() {
        return lines_;
    }

    public static MultiLineString createTfcat( Consumer<Report> reporter,
                                               JSONObject json, Bbox bbox ) {
        JsonTool jtool = new JsonTool( reporter );
        Position[][] lines =
            jtool.asPositionArrayArray( json.opt( "coordinates" ),
                                        "MultiLineString coordinates", true );
        if ( lines == null ) {
            return null;
        }
        else {
            int nl = lines.length;
            for ( int il = 0; il < nl; il++ ) {
                int np = lines[ il ].length;
                if ( np < 2 ) {
                    jtool.report( Level.ERROR, "MLTF",
                                  "MultiLineString[" + il + "]: too few points"
                                + " (" + np + "<2)" );
                    return null;
                }
            }
            return new MultiLineString( json, bbox, lines );
        }
    }
}

