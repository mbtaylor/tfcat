
@FunctionalInterface
public interface Decoder<T> {
    T decode( Reporter reporter, Object json );
}
