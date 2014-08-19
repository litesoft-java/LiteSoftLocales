package org.litesoft.locales.shared.tables.support;

import org.litesoft.commonfoundation.indent.*;
import org.litesoft.commonfoundation.typeutils.*;

import java.util.*;

public class LocaleIssues {
    private final List<String> mNoTranslationSet = Lists.newArrayList();
    private final List<String> mNoTranslations = Lists.newArrayList();
    private final List<String> mTooSimilar = Lists.newArrayList();
    private final IndentableWriter mWriter;
    private final boolean mSuppressTooSimilar;

    public LocaleIssues( IndentableWriter pWriter, boolean pSuppressTooSimilar ) {
        mWriter = pWriter;
        mSuppressTooSimilar = pSuppressTooSimilar;
    }

    public void noTranslationSet( String pText_en_us ) {
        mNoTranslationSet.add( '"' + pText_en_us + '"' );
    }

    public void noTranslation( String pText_en_us ) {
        mNoTranslations.add( '"' + pText_en_us + '"' );
    }

    public void tooSimilarToEnglish( String pText_en_us, String pTextLocale ) {
        if ( !mSuppressTooSimilar ) {
            mTooSimilar.add( "--- " + '"' + pText_en_us + '"' );
            mTooSimilar.add( "--> " + '"' + pTextLocale + '"' );
        }
    }

    public void report() {
        dump( mNoTranslationSet, "No TranslationSet" );
        dump( mNoTranslations, "No Translations" );
        dump( mTooSimilar, "TooSimilar" );
        mWriter.close();
    }

    private void dump( List<String> pLines, String pLabel ) {
        if ( !pLines.isEmpty() ) {
            mWriter.indent();
            mWriter.printLn( pLabel, ":" );
            mWriter.indent();
            for ( String zLine : pLines ) {
                mWriter.printLn( zLine );
            }
            mWriter.outdent();
            mWriter.outdent();
        }
    }
}
