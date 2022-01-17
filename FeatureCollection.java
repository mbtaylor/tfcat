
import java.util.Map;
import org.json.JSONObject;

public class FeatureCollection extends TfcatObject {

    private final Feature[] features_;
    private final Map<String,Field> fieldMap_;

    public FeatureCollection( JSONObject json, Bbox bbox,
                              Map<String,Field> fieldMap, Feature[] features ) {
        super( json, "FeatureCollection", bbox );
        features_ = features;
        fieldMap_ = fieldMap;
    }

    public Feature[] getFeatures() {
        return features_;
    }

    public Map<String,Field> getFields() {
        return fieldMap_;
    }
}
