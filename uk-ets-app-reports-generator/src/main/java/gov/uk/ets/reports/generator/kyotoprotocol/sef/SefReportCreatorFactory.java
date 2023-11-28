package gov.uk.ets.reports.generator.kyotoprotocol.sef;

import gov.uk.ets.reports.generator.kyotoprotocol.KyotoProtocolParams;

/**
 * A sef report creator factory
 *
 * @author kattoulp
 *
 */
public class SefReportCreatorFactory {

    private SefReportCreatorFactory(){}

    /**
     * Returns a sef report creator
     */
    public static AbstractSefReportCreator getReportCreator(KyotoProtocolParams params) {
        
        if(params.getCommitmentPeriod().isEmpty()) {
            return null;
        } else {
            switch (params.getCommitmentPeriod().get().shortValue()) {
                case 1:
                    return new CreateSefReport(params);
                case 2:
                    return new CreateSef2Report(params);
                default:
                    return null;
            }
        }
    }

}
