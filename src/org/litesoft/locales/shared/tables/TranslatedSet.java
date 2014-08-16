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
    private static LocalizationSuppliers sSupplierValidator;

    TranslatedSet() {
        super( "Translated Text" );
    }

    public TranslatedSet( String pTranslatedText ) {
        this();
        setTranslatedText( pTranslatedText );
    }

    public TranslatedSet( String pTranslatedText, LocalizationSupplier pSupplier ) {
        this( pTranslatedText );
        add( pSupplier );
    }

    public static void setSupplierValidator( LocalizationSuppliers pSupplierValidator ) {
        sSupplierValidator = pSupplierValidator;
    }

    private static LocalizationSuppliers getSupplierValidator() {
        LocalizationSuppliers zSupplierValidator = sSupplierValidator;
        if ( zSupplierValidator == null ) {
            zSupplierValidator = LocalizationSuppliers.SUPPLIER.get();
        }
        return Confirm.isNotNull( "SupplierValidator not set", zSupplierValidator );
    }

    public String getTranslatedText() {
        return getRawKey();
    }

    public void setTranslatedText( String pTranslatedText ) {
        updateKey( pTranslatedText );
    }

    public List<LocalizationSupplier> getSuppliers() {
        LocalizationSuppliers zValidator = getSupplierValidator();
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

    @Override
    protected String validateNewKey( String pWhat, String pKey ) {
        return Confirm.isNotNull( pWhat, pKey );
    }

    public boolean hasSupplier( LocalizationSupplier pSupplier ) {
        return suppliers.contains( pSupplier.getName() );
    }

    public boolean hasSuppliers() {
        return !suppliers.isEmpty();
    }

    public String getTranslationFor( String pDefault ) {
        String zText = ConstrainTo.notNull( translatedText );
        return (zText.length() > 0) ? zText : pDefault;
    }

    public TranslatedSet orBetter( TranslatedSet them ) {
        return (this.calcConfidencePercentage() < them.calcConfidencePercentage()) ? them : this;
    }

    private int calcConfidencePercentage() {
        List<LocalizationSupplier> zSuppliers = getSuppliers();
        return zSuppliers.isEmpty() ? 0 : LocalizationSuppliers.calcConfidencePercentage( zSuppliers );
    }
}
