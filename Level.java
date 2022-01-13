
public enum Level {

    INFO,
    WARNING,
    ERROR;

    private final char chr_;

    Level() {
        chr_ = toString().charAt( 0 );
    }

    public char getChar() {
        return chr_;
    }
}
