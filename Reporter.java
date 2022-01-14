
public interface Reporter {
    void report( String msg );
    Reporter createReporter( String subContext );
}
