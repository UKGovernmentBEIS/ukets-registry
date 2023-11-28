package gov.uk.ets.registry.api.transaction.checks;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class, responsible for mapping values from the business-checks.properties file.
 */
@Getter
@Setter
@Configuration
@PropertySource("classpath:business-checks.properties")
@ConfigurationProperties(prefix = "specific")
@EnableConfigurationProperties
public class BusinessChecksConfiguration {

    /**
     * Business checks for common checks.
     */
    @Value("${CommonChecks}")
    private String[] commonChecks;

    /**
     * Business checks for issue of AAUs and RMUs.
     */
    @Value("${specific.checks.IssueOfAAUsAndRMUs}")
    private String[] issueOfAausAndRmus;

    /**
     * Business checks for transfer to SOP for first external AAU transfer.
     */
    @Value("${specific.checks.TransferToSOPforFirstExtTransferAAU}")
    private String[] transferToSopforFirstExtTransferAau;

    /**
     * Business checks for Retirement.
     */
    @Value("${specific.checks.Retirement}")
    private String[] retirement;

    /**
     * Business checks for Art. 3.7ter cancellation of AAU transfer.
     */
    @Value("${specific.checks.Art37Cancellation}")
    private String[] art37Cancellation;

    /**
     * Business checks for Voluntary cancellation of AAU transfer.
     */
    @Value("${specific.checks.CancellationKyotoUnits}")
    private String[] cancellationKyotoUnits;

    /**
     * Business checks for Mandatory cancellation of Kyoto units.
     */
    @Value("${specific.checks.MandatoryCancellation}")
    private String[] mandatoryCancellation;

    /**
     * Business checks for Expiry Date Change of tCER or lCER units.
     */
    @Value("${specific.checks.ExpiryDateChange}")
    private String[] expiryDateChange;

    /**
     * Business checks for Replacement of tCER or lCER.
     */
    @Value("${specific.checks.Replacement}")
    private String[] replacement;

    /**
     * Business checks for Ambition increase cancellation of AAU transfer.
     */
    @Value("${specific.checks.AmbitionIncreaseCancellation}")
    private String[] ambitionIncreaseCancellation;

    /**
     * Business checks for internal transfer.
     */
    @Value("${specific.checks.InternalTransfer}")
    private String[] internalTransfer;

    /**
     * Business checks for external transfer.
     */
    @Value("${specific.checks.ExternalTransfer}")
    private String[] externalTransfer;

    /**
     * Business checks for carry over (AAU).
     */
    @Value("${specific.checks.CarryOver_AAU}")
    private String[] carryOverAAU;

    /**
     * Business checks for CP1 Conversion.
     */
    @Value("${specific.checks.ConversionCP1}")
    private String[] conversionCP1;

    /**
     * Business checks for Conversion of AAUs or RMUs to ERUs prior to Transfer to SOP (Conversion A).
     */
    @Value("${specific.checks.ConversionA}")
    private String[] conversionA;

    /**
     * Business checks for Conversion of AAUs or RMUs to ERUs after the Transfer to SOP (Conversion B).
     */
    @Value("${specific.checks.ConversionB}")
    private String[] conversionB;

    @Value("${specific.checks.TransferToSOPForConversionOfERU}")
    private String[] transferToSOPForConversionOfERU;

    /**
     * Business checks for carry over (CER / ERU).
     */
    @Value("${specific.checks.CarryOver_CER_ERU_FROM_AAU}")
    private String[] carryOverCER;

    /**
     * Business checks for inbound transfers.
     */
    @Value("${specific.checks.InboundTransfer}")
    private String[] inboundTransfer;

    /**
     * Business checks for issue allowances.
     */
    @Value("${specific.checks.IssueAllowances}")
    private String[] issueAllowances;

    /**
     * Business checks for central transfer allowances.
     */
    @Value("${specific.checks.CentralTransferAllowances}")
    private String[] centralTransferAllowances;

    /**
     * Business checks for transfer allowances.
     */
    @Value("${specific.checks.TransferAllowances}")
    private String[] transferAllowances;

    /**
     * Business checks for auction delivery allowances.
     */
    @Value("${specific.checks.AuctionDeliveryAllowances}")
    private String[] auctionDeliveryAllowances;

    /**
     * Business checks for surrender allowances.
     */
    @Value("${specific.checks.SurrenderAllowances}")
    private String[] surrenderAllowances;

    /**
     * Business checks for return of excess allocations.
     */
    @Value("${specific.checks.ExcessAllocation}")
    private String[] excessAllocation;
    
    /**
     * Business checks for closure transfer.
     */
    @Value("${specific.checks.ClosureTransfer}")
    private String[] closureTransfer;
    
    private Map<String, String[]> checksMap = new HashMap<>();

    public Map<String, String[]> getChecks() {
        return checksMap;
    }
}
