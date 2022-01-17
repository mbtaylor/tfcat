
import org.json.JSONObject;

public class Feature extends TfcatObject {

    private final Geometry<?> geometry_;
    private final String id_;
    private final JSONObject properties_;

    public Feature( JSONObject json, Bbox bbox, Geometry<?> geometry,
                    String id, JSONObject properties ) {
        super( json, "Feature", bbox );
        geometry_ = geometry;
        id_ = id;
        properties_ = properties;
    }

    public Geometry<?> getGeometry() {
        return geometry_;
    }

    public String getId() {
        return id_;
    }

    public JSONObject getProperties() {
        return properties_;
    }
}
