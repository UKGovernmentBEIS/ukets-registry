package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.itl.notice.ITLNoticeService;
import gov.uk.ets.registry.api.itl.notice.domain.ITLNotification;
import gov.uk.ets.registry.api.itl.notice.domain.ITLNotificationHistory;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeType;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckError;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckException;
import gov.uk.ets.registry.api.transaction.common.FullAccountIdentifierParser;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import gov.uk.ets.registry.api.transaction.exception.TransactionExecutionException;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.LockModeType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for reserving units.
 */
@Log4j2
@Service
@AllArgsConstructor
public class UnitReservationService {

    /**
     * Service for accounts.
     */
    private final TransactionAccountService transactionAccountService;

    /**
     * Persistence service for transactions.
     */
    private final TransactionPersistenceService transactionPersistenceService;

    /**
     * Service for splitting unit blocks.
     */
    private final UnitSplitService unitSplitService;

    /**
     * Repository for unit blocks.
     */
    private final UnitBlockRepository unitBlockRepository;

    /**
     * Service for projects.
     */
    private final ProjectService projectService;

    private final ITLNoticeService itlNoticeService;

    /**
     * Performs reservation of the provided units, splitting if necessary.
     *
     * @param transaction The transaction details
     */
    @Transactional
    public void reserveUnits(TransactionSummary transaction) {

        // Reservation is necessary only for transfers started from inside the registry
        if (!Constants.isInternalRegistry(transaction.getTransferringRegistryCode())) {
            return;
        }

        transactionAccountService.lockAccount(transaction.getTransferringAccountIdentifier());
        UnitReservationResult result = reserveUnitBlocks(transaction, false);
        if (result.technicalLimitExceeded()) {
            BusinessCheckError error = new BusinessCheckError(3004,
                "The amount requested exceeds the maximum number of blocks (3000) accepted by ITL in a single transaction.");
            throw new BusinessCheckException(error);

        } else if (!result.isSuccess()) {
            BusinessCheckError error = new BusinessCheckError(3005,
                "The requested quantity exceeds the current  account balance for the unit type being transferred.");
            throw new BusinessCheckException(error);
        }
    }

    /**
     * Reserves unit blocks for the provided transaction.
     *
     * @param transaction The transaction.
     */
    private UnitReservationResult reserveUnitBlocks(TransactionSummary transaction, boolean reserveForReplacement) {
        UnitReservationResult result = new UnitReservationResult();

        try {
            String transactionIdentifier = transaction.getIdentifier();

            for (TransactionBlockSummary block : transaction.getBlocks()) {

            	Long transferringAccountIdentifier = transaction.getTransferringAccountIdentifier();
            	//For the to be replaced units the account identifier may be specified by the user.
            	if(reserveForReplacement && Optional.ofNullable(transaction.getToBeReplacedBlocksAccountFullIdentifier()).isPresent()) {
            		FullAccountIdentifierParser fullAccountParser = FullAccountIdentifierParser.getInstance(transaction.getToBeReplacedBlocksAccountFullIdentifier());
            		Optional<Long> accountIdentifierOptional = fullAccountParser.getIdentifier();
             		if(accountIdentifierOptional.isPresent()) {
                		transferringAccountIdentifier = fullAccountParser.getIdentifier().isPresent() ? accountIdentifierOptional.get() : transferringAccountIdentifier;
            		} else {
            			//Should never enter in here
            			throw new IllegalStateException("FullAccountIdentifier could not be parsed.");
            		}
            	}
                Map<SelectionCriteria, Object> selectionCriteria = constructCriteria(
                		transferringAccountIdentifier, block, transaction.getType());
                List<UnitBlock> selectedUnitBlocks = selectUnitBlocks(selectionCriteria, null, reserveForReplacement);

                long quantity = block.calculateQuantity();
                long reservedQuantity =
                    proceedToReservation(transactionIdentifier, selectedUnitBlocks, quantity, result, reserveForReplacement);

                if (reservedQuantity < quantity) {
                    log.info(
                        "There are not enough unit blocks to cover the requested quantity. Quantity {}, Found {}, Criteria {}",
                        quantity, reservedQuantity, selectionCriteria);
                    result.setSuccess(false);
                    break;
                }
            }
        } catch (Exception exc) {
            throw new TransactionExecutionException(getClass(), "Exception when reserving a unit block.", exc);
        }

        return result;
    }

    /**
     * Proceeds to reservation.
     *
     * @param transactionIdentifier The transaction identifier.
     * @param selectedUnitBlocks    The selected unit blocks.
     * @param quantity              The quantity to cover.
     * @param unitReservationResult The result of the reservation.
     * @param replacement           Whether this reservation is about units to be replaced.
     * @return the number of unit blocks reserved.
     */
    private long proceedToReservation(String transactionIdentifier, List<UnitBlock> selectedUnitBlocks, long quantity,
                                      UnitReservationResult unitReservationResult, boolean replacement) {
        long total = 0;
        for (UnitBlock unitBlock : selectedUnitBlocks) {
            total = total + unitBlock.getQuantity();

            if (total < quantity) {
                // Reserve the unit block and continue.
                reserveBlock(unitBlock, transactionIdentifier, unitReservationResult, replacement);

            } else {
                if (total > quantity) {
                    // Reserve only a part of the block; split the rest.
                    long newEnd = unitBlock.getEndBlock() - total + quantity;
                    unitSplitService.split(unitBlock, false, newEnd);
                }
                reserveBlock(unitBlock, transactionIdentifier, unitReservationResult, replacement);
                break;
            }
        }
        return total;
    }

    /**
     * Retrieves the originating country code.
     *
     * @param block         The transaction block.
     * @param projectNumber The project number.
     * @return the country code.
     */
    private String getOriginatingCountryCode(TransactionBlockSummary block, String projectNumber) {
        String originatingCountryCode = block.getOriginatingCountryCode();
        String projectParty = projectService.extractProjectParty(projectNumber);
        if (projectParty != null) {
            originatingCountryCode = projectParty;
        }
        return originatingCountryCode;
    }

    /**
     * Constructs the query selection criteria.
     *
     * @param transferringAccountIdentifier The transferring account identifier.
     * @param block                         The transaction block.
     * @return a criteria map.
     */
    private Map<SelectionCriteria, Object> constructCriteria(Long transferringAccountIdentifier,
                                                             TransactionBlockSummary block,
                                                             TransactionType transactionType) {

        Map<SelectionCriteria, Object> criteria = new EnumMap<>(SelectionCriteria.class);

        CommitmentPeriod applicablePeriod = block.getApplicablePeriod();
        CommitmentPeriod originalPeriod = block.getOriginalPeriod();
        String projectNumber = block.getProjectNumber();
        UnitType unitType = block.getType();

        criteria.put(SelectionCriteria.UNIT_TYPE, unitType);
        criteria.put(SelectionCriteria.APPLICABLE_PERIOD, applicablePeriod);
        criteria.put(SelectionCriteria.ORIGINAL_PERIOD, originalPeriod);
        if (StringUtils.isNotBlank(projectNumber) && unitType.isRelatedWithProject()) {
            criteria.put(SelectionCriteria.PROJECT_IDENTIFIER, projectNumber);
        }
        if (unitType.isRelatedWithProject()) {
            criteria.put(SelectionCriteria.ORIGINATING_COUNTRY_CODE, getOriginatingCountryCode(block, projectNumber));
        }
        if (transactionType.getOriginatingCountryCode() != null) {
            criteria.put(SelectionCriteria.ORIGINATING_COUNTRY_CODE, transactionType.getOriginatingCountryCode());
        }

        criteria.put(SelectionCriteria.ACCOUNT_IDENTIFIER, transferringAccountIdentifier);
        

        // Define SOP for units that are subject to SOP.
        if (block.getSubjectToSop() != null && unitType.isSubjectToSop() &&
                CommitmentPeriod.CP2.equals(originalPeriod) &&
                CommitmentPeriod.CP2.equals(applicablePeriod)) {
            criteria.put(SelectionCriteria.SUBJECT_TO_SOP, block.getSubjectToSop());
        }

        if (block.getEnvironmentalActivity() != null) {
            criteria.put(SelectionCriteria.ENVIRONMENTAL_ACTIVITY, block.getEnvironmentalActivity());
        }

        return criteria;
    }

    /**
     * Selects the unit blocks, based on the provided criteria.
     *
     * @param criteria             The selection criteria.
     * @param lockMode             The lock mode.
     * @return some unit blocks.
     */
    private List<UnitBlock> selectUnitBlocks(Map<SelectionCriteria, Object> criteria, LockModeType lockMode,
                                             boolean reserveForReplacement) {

        StringBuilder query = new StringBuilder("select ub from ").append(UnitBlock.class.getName()).append(" ub where 1 = 1");

        if (!criteria.containsKey(SelectionCriteria.RESERVED_FOR_TRANSACTION)) {
            query.append(" and ub.reservedForTransaction is null");
        }

        if (reserveForReplacement) {
            query.append(" and ub.reservedForReplacement is null");
            query.append(" and (ub.replaced = false or ub.replaced is null)");
        }

        for (Map.Entry<SelectionCriteria, Object> parameter : criteria.entrySet()) {
            Object value = parameter.getValue();
            if (value != null) {
                SelectionCriteria key = parameter.getKey();
                String parameterClause = key.getQuery();

                if (SelectionCriteria.EXCLUDE_APPLICABLE_PERIOD.equals(key)) {
                    query.append(" and ").append(SelectionCriteria.APPLICABLE_PERIOD).append(" <> :").append(key);

                } else {
                    if (parameterClause != null) {
                        query.append(" and ").append(parameterClause).append(" = :").append(key);
                    }
                }
            }
        }

        query.append(" order by ub.acquisitionDate asc, ub.originatingCountryCode asc, ub.startBlock asc ");

        return transactionPersistenceService.find(query.toString(), criteria, lockMode);
    }

    /**
     * Reserves the provided unit block.
     *
     * @param unitBlock             The unit block.
     * @param transactionIdentifier The transaction identifier.
     * @param unitReservationResult The unit reservation result.
     */
    private void reserveBlock(UnitBlock unitBlock, String transactionIdentifier,
                              UnitReservationResult unitReservationResult, boolean reserveForReplacement) {
        if (reserveForReplacement) {
            unitBlock.setReservedForReplacement(transactionIdentifier);

        } else {
            unitBlock.setReservedForTransaction(transactionIdentifier);
        }
        unitReservationResult.increaseCounter();
    }

    /**
     * Releases any reserved blocks for this transaction.
     *
     * @param transactionIdentifier The transaction identifier.
     */
    @Transactional
    public void releaseBlocks(String transactionIdentifier) {
        unitBlockRepository.releaseReservedBlocks(transactionIdentifier);
    }

    /**
     * Releases any reserved blocks for this Replacement transaction.
     *
     * @param transactionIdentifier The transaction identifier.
     */
    @Transactional
    public void releaseBlocksForReplacement(String transactionIdentifier) {
        unitBlockRepository.releaseReservedForReplacementBlocks(transactionIdentifier);
    }

    @Transactional
    public void reserveUnitsForReplacement(TransactionSummary transaction) {
        ITLNotification notification = itlNoticeService.getNotification(transaction.getItlNotification().getNotificationIdentifier());

        UnitReservationResult result = new UnitReservationResult();
        if (NoticeType.IMPENDING_EXPIRY_OF_TCER_AND_LCER.equals(notification.getType())) {
        	
            List<UnitBlock> blocksToBeReplacedFromNotice = itlNoticeService.getUnitBlocksToBeReplaced(transaction.getItlNotification().getNotificationIdentifier());
            
        	if(Optional.ofNullable(transaction.getToBeReplacedBlocksAccountFullIdentifier()).isPresent()) {
        		FullAccountIdentifierParser fullAccountParser = FullAccountIdentifierParser.getInstance(transaction.getToBeReplacedBlocksAccountFullIdentifier());
        		List<UnitBlock> blocksToBeReplacedFromUserSelectedAccount = blocksToBeReplacedFromNotice.stream().filter(b -> b.getAccountIdentifier().equals(fullAccountParser.getIdentifier().get())).collect(Collectors.toList());
                
                Long totalHoldingsToBeReplacedFromUserSelectedAccount = blocksToBeReplacedFromUserSelectedAccount.
                stream().
                collect(Collectors.summingLong(UnitBlock::getQuantity));
                
                if(totalHoldingsToBeReplacedFromUserSelectedAccount < transaction.calculateQuantity()) {
                	 throw new TransactionExecutionException(getClass(), "The requested quantity of lCERs or tCERs to replace exceeds the to be replaced account balance.", new IllegalArgumentException());
                }
                
        		proceedToReservation(transaction.getIdentifier(), blocksToBeReplacedFromUserSelectedAccount, transaction.calculateQuantity(), result, true);
        	} else {
                proceedToReservation(transaction.getIdentifier(), blocksToBeReplacedFromNotice, transaction.calculateQuantity(), result, true);	
        	}

        } else if (NoticeType.REVERSAL_OF_STORAGE_FOR_CDM_PROJECT.equals(notification.getType()) ||
            NoticeType.NON_SUBMISSION_OF_CERTIFICATION_REPORT_FOR_CDM_PROJECT.equals(notification.getType())) {

            ITLNotificationHistory data = itlNoticeService.getNotificationData(transaction.getItlNotification().getNotificationIdentifier());

            TransactionBlockSummary blockSummary = TransactionBlockSummary.builder()
                .projectNumber(data.getProjectNUmber())
                .type(UnitType.LCER)
                .quantity(String.valueOf(transaction.calculateQuantity()))
                .build();

            TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
                .identifier(transaction.getIdentifier())
                .type(transaction.getType())
                .toBeReplacedBlocksAccountFullIdentifier(transaction.getToBeReplacedBlocksAccountFullIdentifier())
                .blocks(Collections.singletonList(blockSummary))
                .build();

            reserveUnitBlocks(transactionSummary, true);
        }
    }


}
