
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BasicReporter implements Consumer<Report> {

    private List<Report> reports_;

    BasicReporter() {
        reports_ = new ArrayList<Report>();
    }

    public List<Report> getReports() {
        return reports_;
    }

    public void accept( Report report ) {
        reports_.add( report );
    }
}
