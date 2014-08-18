package org.litesoft.locales.shared.tables;

import org.litesoft.commonfoundation.annotations.*;
import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.indent.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.locales.shared.tables.support.*;

import java.util.*;

public final class LocaleTranslation extends AbstractKeyedOwned<LocaleTranslation> {
    @Override
    protected synchronized String getRawKey() {
        return enUStext;
    }

    @Override
    protected synchronized void setRawKey( String pKey ) {
        enUStext = pKey;
    }

    private String enUStext;
    private final List<TranslatedSet> translatedSets = Lists.newArrayList();
    private final transient KeyedOwnedManager<TranslatedSet> mManager = new KeyedOwnedManager<TranslatedSet>( "TranslatedSets" ) {
        @Override
        protected List<TranslatedSet> getOwnedList() {
            return translatedSets;
        }
    };

    LocaleTranslation() {
        super( "en_US text" );
    }

    public LocaleTranslation( String pEnUStext, TranslatedSet... pTranslatedSets ) {
        this();
        setEnUStext( pEnUStext );
        addAll( pTranslatedSets );
    }

    public String getEnglishUStext() {
        return getRawKey();
    }

    public void setEnUStext( String pEnUStext ) {
        updateKey( pEnUStext );
    }

    public synchronized TranslatedSet[] getTranslatedSets() {
        return translatedSets.toArray( new TranslatedSet[translatedSets.size()] );
    }

    public void clearTranslatedSets() {
        mManager.clear();
    }

    public int getTranslatedSetsSize() {
        return mManager.size();
    }

    public TranslatedSet getTranslatedSet( String pKey ) {
        return mManager.get( pKey );
    }

    public void addAll( TranslatedSet... pTranslatedSets ) {
        mManager.addAll( pTranslatedSets );
    }

    public void addAll( List<TranslatedSet> pTranslatedSets ) {
        mManager.addAll( pTranslatedSets );
    }

    public TranslatedSet add( TranslatedSet pTranslatedSet ) {
        return mManager.add( pTranslatedSet );
    }

    public void removeAll( TranslatedSet... pTranslatedSets ) {
        mManager.removeAll( pTranslatedSets );
    }

    public void removeAll( List<TranslatedSet> pTranslatedSets ) {
        mManager.removeAll( pTranslatedSets );
    }

    public boolean remove( TranslatedSet pTranslatedSet ) {
        return mManager.remove( pTranslatedSet );
    }

    @Override
    protected void appendNonKeys( @NotNull IndentableWriter pWriter ) {
        mManager.appendTo( pWriter );
    }

    @Override
    protected String validateNewKey( String pWhat, String pKey ) {
        return Confirm.isNotNull( pWhat, pKey );
    }

    public void set( String pTranslatedText, LocalizationSupplier pSupplier ) {
        TranslatedSet zSetByText = locateSet( pTranslatedText );
        TranslatedSet zSetBySupplier = locateSet( pSupplier );
        if ( zSetBySupplier == null ) {
            addSupplier( pSupplier, zSetByText, pTranslatedText );
        } else if ( zSetBySupplier != zSetByText ) {
            removeSupplier( pSupplier, zSetBySupplier );
            addSupplier( pSupplier, zSetByText, pTranslatedText );
        }
    }

    private void addSupplier( LocalizationSupplier pSupplier, TranslatedSet pSetByText, String pTranslatedText ) {
        if ( pSetByText == null ) {
            add( new TranslatedSet( pTranslatedText, pSupplier ) );
        } else {
            pSetByText.add( pSupplier );
        }
    }

    private void removeSupplier( LocalizationSupplier pSupplier, TranslatedSet pSetBySupplier ) {
        pSetBySupplier.remove( pSupplier );
        if ( !pSetBySupplier.hasSuppliers() ) {
            remove( pSetBySupplier );
        }
    }

    private TranslatedSet locateSet( LocalizationSupplier pSupplier ) {
        for ( TranslatedSet zSet : translatedSets ) {
            if ( zSet.hasSupplier( pSupplier ) ) {
                return zSet;
            }
        }
        return null;
    }

    private TranslatedSet locateSet( String pTranslatedText ) {
        return mManager.get( pTranslatedText );
    }

    public String getTranslationFor( LocaleIssues pIssueCollector ) {
        TranslatedSet zBestSet = findBestSet( pIssueCollector );
        return (zBestSet == null) ? enUStext : zBestSet.getTranslationFor( enUStext );
    }

    private TranslatedSet findBestSet( LocaleIssues pIssueCollector ) {
        if ( translatedSets.isEmpty() ) {
            pIssueCollector.noTranslationSet( enUStext );
            return null;
        }
        Iterator<TranslatedSet> zIt = translatedSets.iterator();
        TranslatedSet zBestSet = evaluateForSimilarityToEnglish( zIt.next(), pIssueCollector );
        while ( zIt.hasNext() ) {
            zBestSet = zBestSet.orBetter( evaluateForSimilarityToEnglish( zIt.next(), pIssueCollector ) );
        }
        return zBestSet;
    }

    private TranslatedSet evaluateForSimilarityToEnglish( TranslatedSet pSet, LocaleIssues pIssueCollector ) {
        String zTranslatedText = pSet.getTranslatedText();
        if ( Currently.significant( zTranslatedText ) ) {
            if ( isTooSimilar( enUStext, zTranslatedText ) ) {
                pIssueCollector.tooSimilarToEnglish( enUStext, zTranslatedText );
            }
        }
        return pSet;
    }

    protected boolean isTooSimilar( String pEnUStext, String pTranslatedText ) {
        return Similarizer.INSTANCE.similarize( pEnUStext ).equals( Similarizer.INSTANCE.similarize( pTranslatedText ) );
    }

    private static final class Similarizer {
        private static final String SPECIAL_PUNC = "()[]{}<>";
        public static final char REG_PUNC = '|';
        public static final Similarizer INSTANCE = new Similarizer();

        enum Mode {
            AlphaNumeric() {
                @Override
                public boolean isMember( char c ) {
                    return Character.isAlphabetic( c ) || (('0' <= c) && (c <= '9'));
                }

                @Override
                protected char transform( char c ) {
                    return Character.toLowerCase( c );
                }
            },
            SpecialPunc() {
                @Override
                public boolean isMember( char c ) {
                    return (-1 != SPECIAL_PUNC.indexOf( c ));
                }
            },
            RegPunc() {
                @Override
                public boolean isMember( char c ) {
                    return true;
                }

                @Override
                protected void addNthMember( char c, StringBuilder sb ) {
                    // Only Add the First
                }

                @Override
                protected char transform( char c ) {
                    return REG_PUNC;
                }
            },
            Start() {
                @Override
                public boolean isMember( char c ) {
                    return false;
                }
            };

            abstract public boolean isMember( char c );

            private static Mode findMode( char c ) {
                for ( Mode zMode : values() ) {
                    if ( zMode.isMember( c ) ) {
                        return zMode;
                    }
                }
                throw new IllegalStateException( "No Mode accepted: " + c );
            }

            public Mode add( char c, StringBuilder sb ) {
                Mode zMode = findMode( c );
                if ( zMode == this ) {
                    addNthMember( c, sb );
                } else {
                    zMode.add1stMember( c, sb );
                }
                return zMode;
            }

            private void add1stMember( char c, StringBuilder sb ) {
                sb.append( transform( c ) );
            }

            protected void addNthMember( char c, StringBuilder sb ) {
                sb.append( transform( c ) );
            }

            protected char transform( char c ) {
                return c;
            }
        }

        public String similarize( String pText ) {
            StringBuilder sb = new StringBuilder();
            pText = " " + pText + " ";
            Mode zMode = Mode.Start;
            for ( int i = 0; i < pText.length(); i++ ) {
                zMode = zMode.add( pText.charAt( i ), sb );
            }
            return sb.toString();
        }
    }
}
