
import java.util.function.Consumer;
import org.json.JSONObject;

@FunctionalInterface
public interface TfcatFactory<T extends TfcatObject> {
    T createTfcat( Consumer<Report> reporter, JSONObject json, Bbox bbox );
}
