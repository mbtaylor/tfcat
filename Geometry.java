
import org.json.JSONObject;

public class Geometry<S> {

    private final JSONObject json_;
    private final String type_;
    private final Bbox bbox_;
    private final S shape_;

    public Geometry( JSONObject json, String type, Bbox bbox, S shape ) {
        json_ = json;
        type_ = type;
        bbox_ = bbox;
        shape_ = shape;
    }

    public JSONObject getJson() {
        return json_;
    }

    public String getType() {
        return type_;
    }

    public Bbox getBbox() {
        return bbox_;
    }

    public S getShape() {
        return shape_;
    }
}

