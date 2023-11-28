select tl.id,tl.last_updated,tl.type, tl.transferring_account_registry_code,
tl.acquiring_account_registry_code,tl.transferring_account_type as transferring_account_type,
tl.acquiring_account_type as acquiring_account_type, n.type, 
tb.unit_type,tb.environmental_activity as block_lulucf_code, 
nh.environmental_activity as notif_lulucf_code, 
tb.original_period, tb.applicable_period, sum(tb.end_block-tb.start_block+1) as amount, tb.project_track,
tb.expiry_date,
case 
      when tl.type = 'TransferToSOPforFirstExtTransferAAU' then 'T'
      else 'F'
end as first_aau_transfer_flag
from transaction tl
inner join transaction_block tb on tl.id=tb.transaction_id
left outer join itl_notification n on tl.notification_identifier = n.identifier
left outer join lateral (select notice_log_id, environmental_activity from itl_notification_history group by notice_log_id, environmental_activity) nh on n.id=nh.notice_log_id
/*non completed transactions are ignored*/
where tl.status in  ('COMPLETED')
/* Internal transfer and expiry date change are ignored unless they change the holding account type*/
and (
  tl.type not in ('ExpiryDateChange','InternalTransfer')
  or
  tl.transferring_account_type != tl.acquiring_account_type
  )
/* only Kyoto units must be considered*/
and tb.unit_type in ('AAU','RMU','ERU_FROM_AAU','ERU_FROM_RMU','CER','TCER','LCER')
/* replaced blocks do not effectively participate to the transaction */
and tb.block_role is null
/* Ignore the issuances of EUAs*/
and tl.type in ('IssueOfAAUsAndRMUs','TransferToSOPforFirstExtTransferAAU','Retirement','CancellationKyotoUnits','MandatoryCancellation','ExpiryDateChange','Replacement','Art37Cancellation','AmbitionIncreaseCancellation','ConversionA','ConversionB','TransferToSOPForConversionOfERU','InternalTransfer','ExternalTransfer','CarryOver_AAU','CarryOver_CER_ERU_FROM_AAU','IssuanceDecoupling','CorrectiveTransactionForReversal_1','Correction','SurrenderKyotoUnits','CancellationAgainstDeletion','ReversalSurrenderKyoto','SetAside')
/*only one reporting CP must be considered but beware carry-over transaction */
and (
tb.applicable_period = ?
or
tl.type IN ('CarryOver_AAU','CarryOver_CER_ERU_FROM_AAU')
)
and tl.last_updated < ?
group by tl.id,tl.last_updated,tl.type, tl.transferring_account_registry_code,
tl.acquiring_account_registry_code,tl.transferring_account_type, 
tl.acquiring_account_type, n.type, 
tb.unit_type,tb.environmental_activity, nh.environmental_activity, tb.original_period, tb.applicable_period, tb.project_track, tb.expiry_date,first_aau_transfer_flag
order by tl.last_updated
