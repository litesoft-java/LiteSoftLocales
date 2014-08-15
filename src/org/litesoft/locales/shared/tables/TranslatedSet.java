package org.litesoft.locales.shared.tables;

import org.litesoft.commonfoundation.annotations.*;
import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.indent.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.locales.shared.tables.support.*;

import java.util.*;

public final class TranslatedSet extends AbstractKeyedOwned<TranslatedSet> {
    @Override
    protected synchronized String getRawKey() {
        return translatedText;
    }

    @Override
    protected synchronized void setRawKey( String pKey ) {
        translatedText = pKey;
    }

    private String translatedText;
    private List<String> suppliers = Lists.newArrayList();
    private transient LocalizationSuppliers mSupplierValidator;

    @Deprecated TranslatedSet() { // reconstitution
    }

    public TranslatedSet( String pTranslatedText ) {
        setTranslatedText( pTranslatedText );
    }

    public void setSupplierValidator( LocalizationSuppliers pSupplierValidator ) {
        mSupplierValidator = pSupplierValidator;
    }

    public String getTranslatedText() {
        return getRawKey();
    }

    public void setTranslatedText( String pTranslatedText ) {
        updateKey( "TranslatedText", pTranslatedText );
    }

    public List<LocalizationSupplier> getSuppliers() {
        LocalizationSuppliers zValidator = Confirm.isNotNull( "SupplierValidator not set", mSupplierValidator );
        List<LocalizationSupplier> zSuppliers = Lists.newArrayList();
        for ( String zSupplierName : suppliers ) {
            LocalizationSupplier zSupplier = zValidator.get( zSupplierName );
            if ( zSupplier != null ) {
                zSuppliers.add( zSupplier );
            }
        }
        return zSuppliers;
    }

    public void add( LocalizationSupplier pSupplier ) {
        String zSupplier = pSupplier.getName();
        if ( !suppliers.contains( zSupplier ) ) {
            suppliers.add( zSupplier );
        }
    }

    public boolean remove( LocalizationSupplier pSupplier ) {
        String zSupplier = pSupplier.getName();
        return suppliers.remove( zSupplier );
    }

    @Override
    protected void appendNonKeys( @NotNull IndentableWriter pWriter ) {
        pWriter.printLn( "Suppliers: ", suppliers );
    }
}
