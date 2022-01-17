
import java.util.List;
import org.json.JSONObject;

public class FeatureCollection extends TfcatObject {

    private final List<Feature> features_;

    public FeatureCollection( JSONObject json, Bbox bbox,
                              List<Feature> features ) {
        super( json, "FeatureCollection", bbox );
        features_ = features;
    }

    public List<Feature> getFeatures() {
        return features_;
    }
}
