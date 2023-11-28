SELECT tl1.identifier,
  tl1.transferring_account_registry_code,
  SUM(tb1.end_block-tb1.start_block+1)
FROM transaction tl1,
  transaction_block tb1
WHERE tl1.id                     =tb1.transaction_id
AND tl1.type                IN ('TransferToSOPforFirstExtTransferAAU','TransferToSOPForConversionOfERU','ExternalTransfer','ExternalTransferCP0','SurrenderKyotoUnits','ReversalSurrenderKyoto','SetAside')
AND tb1.unit_type                      IN ('ERU_FROM_AAU','ERU_FROM_RMU')
AND tl1.status             = 'COMPLETED'
AND tb1.project_track                                = 'TRACK_2'
AND extract (YEAR FROM tl1.last_updated) = ? /* insert reported year here */
AND tl1.last_updated < ? /* insert reported year or report end date here */
AND tb1.applicable_period = ? /* insert reportCP here */
AND NOT EXISTS
  (SELECT 1
  FROM transaction tl2,
    transaction_block tb2
  WHERE tl2.id         = tb2.transaction_id
  AND tl2.identifier          !=tl1.identifier
  AND tl2.type    IN ('TransferToSOPforFirstExtTransferAAU','TransferToSOPForConversionOfERU','ExternalTransfer','ExternalTransferCP0','SurrenderKyotoUnits','ReversalSurrenderKyoto','SetAside')
  AND tb2.unit_type          IN ('ERU_FROM_AAU','ERU_FROM_RMU')
  AND tl2.status = 'COMPLETED'
  AND tb2.originating_country_code = tb1.originating_country_code
  AND tb2.end_block               >=tb1.start_block
  AND tb2.start_block             <= tb1.end_block
  AND tl2.last_updated         < tl1.last_updated
  )
GROUP BY tl1.identifier,
  tl1.transferring_account_registry_code
