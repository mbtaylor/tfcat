
public interface Reporter {
    void report( String msg );
    void checkUcd( String ucd );
    void checkUnit( String unit );
    Reporter createReporter( String subContext );
    Reporter createReporter( int subContext );
}
