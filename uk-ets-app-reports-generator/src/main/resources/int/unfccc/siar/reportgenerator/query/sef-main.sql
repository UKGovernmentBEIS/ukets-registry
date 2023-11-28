select tl.id,tl.last_updated,tl.type, tl.transferring_account_registry_code,
tl.acquiring_account_registry_code,tl.transferring_account_type as transferring_account_type,
tl.acquiring_account_type as acquiring_account_type, n.type, 
tb.unit_type,tb.environmental_activity as block_lulucf_code, nh.environmental_activity as notif_lulucf_code, tb.applicable_period, sum(tb.end_block-tb.start_block+1) as amount, tb.project_track
from transaction tl
inner join transaction_block tb on (tl.id=tb.transaction_id)
left outer join itl_notification n on tl.notification_identifier = n.identifier
left outer join itl_notification_history nh on n.id=nh.notice_log_id
/*non completed transactions are ignored*/
where tl.status in ('COMPLETED','REVERSED')
/* Internal transfer and expiry date change are ignored unless they change the holding account type*/
and (
  tl.type not in ('ExpiryDateChange','InternalTransfer', 'AllocationOfFormerEUA','IssuanceOfFormerEUA', 'CancellationCP0','ConversionOfSurrenderedFormerEUA'
                 ,'CorrectiveTransactionForReversal_1','Correction')
  or
  tl.transferring_account_type !=
  tl.acquiring_account_type
  )
/* only Kyoto units must be considered*/
and tb.unit_type in ('AAU','RMU','ERU_FROM_AAU','ERU_FROM_RMU','CER','TCER','LCER','FORMER_EUA')
/* replaced blocks do not effectively participate to the transaction */
and tb.block_role is null
/* Ignore the issuances of EUAs*/
and tl.type in ('AllocationOfFormerEUA','ConversionOfSurrenderedFormerEUA','RetirementOfSurrenderedFormerEUA','SurrenderAllowances','CancellationCP0', 'RetirementCP0','IssuanceCP0', 'ExternalTransferCP0','ConversionCP1','IssueOfAAUsAndRMUs','TransferToSOPforFirstExtTransferAAU','Retirement','CancellationKyotoUnits','MandatoryCancellation','ExpiryDateChange','Replacement','Art37Cancellation','AmbitionIncreaseCancellation','ConversionA','ConversionB','TransferToSOPForConversionOfERU','InternalTransfer','ExternalTransfer','CarryOver_AAU','CarryOver_CER_ERU_FROM_AAU','IssuanceDecoupling','CorrectiveTransactionForReversal_1','Correction','SurrenderKyotoUnits','CancellationAgainstDeletion','ReversalSurrenderKyoto','SetAside')
/*only one reporting CP must be considered but beware carry-over transaction */
and (
tb.applicable_period = ?
or
tl.type IN ('CarryOver_AAU','CarryOver_CER_ERU_FROM_AAU')
)
and tl.last_updated < ?
group by tl.id,tl.last_updated,tl.type, tl.transferring_account_registry_code,
tl.acquiring_account_registry_code,tl.transferring_account_type, 
tl.acquiring_account_type, N.TYPE, 
tb.unit_type,tb.environmental_activity, nh.environmental_activity, tb.applicable_period, tb.project_track
order by tl.last_updated
