
import org.json.JSONObject;

public class Geometry<S> extends TfcatObject {

    private final S shape_;

    public Geometry( JSONObject json, String type, Bbox bbox, S shape ) {
        super( json, type, bbox );
        shape_ = shape;
    }

    public S getShape() {
        return shape_;
    }
}

