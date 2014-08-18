package org.litesoft.locales.server;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.locales.shared.support.*;
import org.litesoft.server.file.*;

import java.io.*;
import java.util.*;

public class SimpleJSLocaleKeyUpdater {
    public static final String VERSION = "0.9";

    private final String mFrom, mFromAsNestedReference;
    private final String mTo, mToAsNestedReference;

    public SimpleJSLocaleKeyUpdater( String pFrom, String pTo ) {
        mFrom = SimpleJSLocaleEntryStatement.validateKey( Confirm.significant( "From", pFrom ) );
        mTo = SimpleJSLocaleEntryStatement.validateKey( Confirm.significant( "To", pTo ) );
        if ( mFrom.equals( mTo ) ) {
            throw new IllegalArgumentException( "(From) '" + mFrom + "' == '" + mTo + "' (To)" );
        }
        mFromAsNestedReference = "{" + mFrom + "}";
        mToAsNestedReference = "{" + mTo + "}";
    }

    public void processDirectoryTree( File pDir ) {
        for ( RelativeFileIterator zFiles = new RecursiveRelativeFileIterator( pDir ); zFiles.hasNext(); ) {
            processFile( pDir, zFiles.next().getRelativeFilePath() );
        }
    }

    private void processFile( File pDir, String pRelativeFilePath ) {
        if ( pRelativeFilePath.toLowerCase().endsWith( ".js" ) ) {
            System.out.print( "    " + Paths.justTheLastName( pDir.getPath() ) + "/" + pRelativeFilePath + "    " );
            try {
                System.out.print( processFile( new File( pDir, pRelativeFilePath ) ) );
            }
            finally {
                System.out.println();
            }
        }
    }

    private String processFile( File pJSFile ) {
        String[] zLines = UTF_HeaderStripper.fixFirstLine( FileUtils.loadTextFile( pJSFile ) );
        String[] zNewLines = pricessFileLines( zLines );
        FileUtils.Change zChange = FileUtils.storeTextFile( pJSFile, zNewLines );
        return (zChange == null) ? "" : ("---> " + zChange);
    }

    private String[] pricessFileLines( String[] pLines ) {
        String[] zNewLines = new String[pLines.length];
        for ( int i = 0; i < pLines.length; i++ ) {
            zNewLines[i] = processLine( pLines[i] );
        }
        return zNewLines;
    }

    private String processLine( String pLine ) {
        try {
            SimpleJSLocaleEntryStatement zEntry = SimpleJSLocaleEntryStatementLineParser.INSTANCE.parse( pLine );
            return (zEntry == null) ? pLine :
                   new SimpleJSLocaleEntryStatement( morphKey( zEntry.getKey() ), morphValue( zEntry.getValue() ) ).toString();
        }
        catch ( RuntimeException e ) {
            System.out.print( "\n        Problem w/: " + pLine );
            throw e;
        }
    }

    private String morphKey( String pKey ) {
        return mFrom.equals( pKey ) ? mTo : pKey;
    }

    private String morphValue( String pValue ) {
        return Strings.replace( pValue, mFromAsNestedReference, mToAsNestedReference );
    }

    public static void main( String[] args )
            throws IOException {
        System.out.println( "SimpleJSLocaleKeyUpdater vs " + VERSION );
        if ( args.length < 2 ) {
            System.exit( showHelp() );
        }
        String zFrom = Confirm.significant( "From Key (1st Arg)", args[0] );
        String zTo = Confirm.significant( "To Key (2nd Arg)", args[1] );
        SimpleJSLocaleKeyUpdater zUpdater = new SimpleJSLocaleKeyUpdater( zFrom, zTo );
        List<File> zDirs = Lists.newArrayList();
        if ( args.length == 2 ) {
            zDirs.add( new File( "." ).getCanonicalFile() );
        } else {
            for ( int i = 2; i < args.length; i++ ) {
                zDirs.add( assertDir( args, i ) );
            }
        }
        for ( File zDir : zDirs ) {
            zUpdater.processDirectoryTree( zDir );
        }
        System.exit( 0 );
    }

    private static File assertDir( String[] args, int pOffset )
            throws IOException {
        String zReference = args[pOffset];
        String zWhat = "Directory '" + zReference + "' (" + Integers.toNth( pOffset + 1 ) + " Arg)";
        return DirectoryUtils.assertExists( zWhat, new File( Confirm.significant( zWhat, zReference ) ) ).getCanonicalFile();
    }

    private static int showHelp() {
        System.out.println( "A minimum of 2 arguments are needed:" );
        System.out.println( "    1) From Key" );
        System.out.println( "    2) To Key" );
        System.out.println( "    3-n) Directories to process (recursively for all .js files) (default is '.')" );
        return 1;
    }
}
