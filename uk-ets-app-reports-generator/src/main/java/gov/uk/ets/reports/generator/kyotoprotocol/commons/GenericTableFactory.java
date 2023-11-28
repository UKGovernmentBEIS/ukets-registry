package gov.uk.ets.reports.generator.kyotoprotocol.commons;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.*;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.QuantityValueEnum;

/**
 * Provides common methods for RF1 and RF2 table factories
 *
 * @author kattoulp
 *
 */
public class GenericTableFactory {

    static final String NO = QuantityValueEnum.NO.name();
    static final String NA = QuantityValueEnum.NA.name();

    /**
     * Creates a {@link TotalUnitQty} object
     *
     * @return the newly created {@link TotalUnitQty} object
     */
    static TotalUnitQty createTotalUnitQty() {
        TotalUnitQty totalUnitQty = new TotalUnitQty();
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty unitQty = new UnitQty();
            unitQty.setType(unitType);
            unitQty.setValue(NO);
            totalUnitQty.getUnitQty().add(unitQty);
        }
        return totalUnitQty;
    }


    /**
     * Creates a {@link SubTotal} object
     *
     * @return the newly created {@link SubTotal} object
     */
    public static SubTotal createSubTotal() {
        SubTotal subTotal = new SubTotal();
        subTotal.setAdditions(new Additions());
        subTotal.setSubtractions(new Subtractions());

        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty add = new UnitQty();
            add.setType(unitType);
            add.setValue(QuantityValueEnum.NO.name());
            subTotal.getAdditions().getUnitQty().add(add);

            UnitQty sub = new UnitQty();
            sub.setType(unitType);
            sub.setValue(QuantityValueEnum.NO.name());
            subTotal.getSubtractions().getUnitQty().add(sub);
        }
        return subTotal;
    }

    /**
     * Creates a {@link TotalAdditionSubtraction} object.
     *
     * @return the newly created {@link TotalAdditionSubtraction} object.
     */
    static TotalAdditionSubtraction createTotalAdditionSubtraction() {
        TotalAdditionSubtraction total = new TotalAdditionSubtraction();
        total.setAdditions(new Additions());
        total.setSubtractions(new Subtractions());

        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty add = new UnitQty();
            add.setType(unitType);
            add.setValue(QuantityValueEnum.NO.name());
            total.getAdditions().getUnitQty().add(add);

            UnitQty sub = new UnitQty();
            sub.setType(unitType);
            sub.setValue(QuantityValueEnum.NO.name());
            total.getSubtractions().getUnitQty().add(sub);
        }
        return total;
    }


}
