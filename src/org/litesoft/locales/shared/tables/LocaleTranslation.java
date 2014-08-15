package org.litesoft.locales.shared.tables;

import org.litesoft.commonfoundation.annotations.*;
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

    @Deprecated LocaleTranslation() { // reconstitution
    }

    public LocaleTranslation( String pEnUStext, TranslatedSet... pTranslatedSets ) {
        setEnUStext( pEnUStext );
        addAll( pTranslatedSets );
    }

    public String getEnglishUStext() {
        return getRawKey();
    }

    public void setEnUStext( String pEnUStext ) {
        updateKey( "Name", pEnUStext );
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
}
