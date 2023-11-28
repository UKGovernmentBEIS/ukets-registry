SELECT ub.unit_type,
  'GB' as holding_registry,
  extract(year from EXPIRY_DATE) as year,
  SUM(end_block-start_block+1)
FROM unit_block ub inner join account a on ub.account_identifier=a.identifier
WHERE kyoto_account_type IN ('PARTY_HOLDING_ACCOUNT', 'FORMER_OPERATOR_HOLDING_ACCOUNT', 'PERSON_HOLDING_ACCOUNT')
AND ub.unit_type in ('TCER','LCER')
and EXPIRY_DATE < ? /* parameter is reported year or report end date */
GROUP BY ub.unit_type, holding_registry, extract(year from EXPIRY_DATE)