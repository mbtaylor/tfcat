
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class TfcatParser {

    public static void main( String[] args ) throws IOException {
        String usage = "\n   "
                     + TfcatParser.class.getSimpleName() + ":"
                     + " [-help]"
                     + " [-debug]"
                     + " <file>|-"
                     + "\n";
        List<String> argList = new ArrayList<>( Arrays.asList( args ) );
        boolean isDebug = false;
        for ( Iterator<String> argIt = argList.iterator(); argIt.hasNext(); ) {
            String arg = argIt.next();
            if ( arg.startsWith( "-h" ) ) {
                argIt.remove();
                System.out.println( usage );
                return;
            }
            else if ( "-debug".equals( arg ) ) {
                argIt.remove();
                isDebug = true;
            }
        }
        if ( argList.size() != 1 ) {
            System.err.println( usage );
            System.exit( 1 );
        }
        String inFile = argList.get( 0 );
        try ( InputStream in = "-".equals( inFile )
                             ? System.in
                             : new FileInputStream( inFile ) ) {
            BasicReporter reporter = new BasicReporter( isDebug );
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
