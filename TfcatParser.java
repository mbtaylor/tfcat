
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class TfcatParser {

    public static void main( String[] args ) throws IOException {
        String inFile = args[ 0 ];
        try ( InputStream in = "-".equals( inFile )
                             ? System.in
                             : new FileInputStream( inFile ) ) {
            BasicReporter reporter = new BasicReporter();
            try {
                JSONObject json = new JSONObject( new JSONTokener( in ) );
                TfcatObject tfcat = Decoders.TFCAT.decode( reporter, json );
            }
            catch ( JSONException e ) {
                reporter.report( "Bad JSON: " + e.getMessage() );
            }
            List<String> msgs = reporter.getMessages();
            if ( msgs.size() == 0 ) {
                System.out.println( "OK" );
                return;
            }
            else {
                System.out.println( "FAIL (" + msgs.size() + ")" );
                for ( String msg : msgs ) {
                    System.out.println( "    " + msg );
                }
                System.exit( 1 );
            }
        }
    }
}
