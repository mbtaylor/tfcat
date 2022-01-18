
import ari.ucidy.UCD;
import ari.ucidy.UCDParser;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import uk.me.nxg.unity.OneUnit;
import uk.me.nxg.unity.Syntax;
import uk.me.nxg.unity.UnitExpr;
import uk.me.nxg.unity.UnitParser;
import uk.me.nxg.unity.UnitParserException;

public class TfcatParser {

    private static UnaryOperator<String> getUcdChecker() {
        try {
            UCDParser.class.toString();
            Logger.getLogger( "ari.ucidy" ).setLevel( Level.OFF );
            return ucd -> {
                UCD pucd = UCDParser.defaultParser.parse( ucd );
                Iterator<String> errit = pucd.getErrors();
                return errit.hasNext() ? errit.next() : null;
            };
        }
        catch ( Throwable e ) {
            return null;
        }
    }

    private static UnaryOperator<String> getUnitChecker() {
        try {
            UnitParser.class.toString();
            return unit -> {
                Syntax syntax = Syntax.VOUNITS;
                UnitExpr punit;
                try { 
                    punit = new UnitParser( syntax, unit ).getParsed();
                }
                catch ( Exception e ) {
                    return "bad unit \"" + unit + "\" (" + e.getMessage() + ")";
                }
                if ( punit.isFullyConformant( syntax ) ) {
                    return null;
                }
                else {
                    for ( OneUnit word : punit ) {
                        if ( ! word.isRecognisedUnit( syntax ) ) {
                            return "unrecognised unit \"" + word + "\"";
                        }
                        else if ( ! word.isRecommendedUnit( syntax ) ) {
                            return "deprecated unit \"" + word + "\"";
                        }
                    }
                    return "unidentified problem with unit \"" + unit + "\"";
                }
            };
        }
        catch ( Throwable e ) {
            return null;
        }
    }

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
            BasicReporter reporter =
                new BasicReporter( isDebug, getUcdChecker(), getUnitChecker() );
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
