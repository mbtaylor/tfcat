
public enum Level {

    INFO,
    WARNING,
    ERROR;

    private final char chr_;

    Level() {
        chr_ = toString().charAt( 0 );
    }
}
