package org.litesoft.locales.shared.tables;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.locales.shared.tables.support.*;

import java.util.*;

public class LocalizationSuppliers extends AbstractKeyedOwner<LocalizationSupplier> {
    private List<LocalizationSupplier> suppliers = Lists.newArrayList();

    public LocalizationSuppliers() {
        super( "Suppliers" );
    }

    @Override
    protected List<LocalizationSupplier> getOwned() {
        return suppliers;
    }

    public synchronized LocalizationSupplier[] getSuppliers() {
        return suppliers.toArray( new LocalizationSupplier[suppliers.size()] );
    }

    public static int calcConfidencePercentage( List<LocalizationSupplier> pSuppliers ) {
        pSuppliers = ConstrainTo.notNull( pSuppliers );
        return calcConfidencePercentage( pSuppliers.toArray( new LocalizationSupplier[pSuppliers.size()] ) );
    }

    public static int calcConfidencePercentage( LocalizationSupplier... pSuppliers ) {
        Confirm.isNotNullOrEmptyAndHasNoNullEntries( "Suppliers", pSuppliers );
        int zPercentage = pSuppliers[0].getConfidencePercentage();
        if ( (zPercentage == 100) || (pSuppliers.length == 1) ) {
            return zPercentage;
        }
        double zDoubtFactor = toDoubtFactor( zPercentage );
        for ( int i = 1; i < pSuppliers.length; i++ ) {
            if ( 100 == (zPercentage = pSuppliers[i].getConfidencePercentage()) ) {
                return zPercentage;
            }
            zDoubtFactor *= toDoubtFactor( zPercentage );
        }
        return 100 - (int) (100 * zDoubtFactor);
    }

    private static double toDoubtFactor( int pPercentage ) {
        return ((double) (100 - pPercentage)) / 100.0;
    }
}
