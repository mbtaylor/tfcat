
import org.json.JSONObject;

public class FeatureCollection extends TfcatObject {

    private final Feature[] features_;
    private final Field[] fields_;

    public FeatureCollection( JSONObject json, Bbox bbox,
                              Feature[] features, Field[] fields ) {
        super( json, "FeatureCollection", bbox );
        features_ = features;
        fields_ = fields;
    }

    public Feature[] getFeatures() {
        return features_;
    }

    public Field[] getFields() {
        return fields_;
    }
}
