
import org.json.JSONObject;

public abstract class TfcatObject {

    private final JSONObject json_;
    private final String type_;
    private final Bbox bbox_;

    protected TfcatObject( JSONObject json, String type, Bbox bbox ) {
        json_ = json;
        type_ = type;
        bbox_ = bbox;
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
}
