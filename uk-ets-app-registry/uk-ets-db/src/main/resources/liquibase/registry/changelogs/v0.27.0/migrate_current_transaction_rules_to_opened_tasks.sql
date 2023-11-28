UPDATE task t
SET difference = difference::jsonb ||
                 ('{"currentRule1": ' ||
                  (SELECT coalesce(a.approval_second_ar_required, true) FROM account a WHERE t.account_id = a.id) || '}')::jsonb ||
                 ('{"currentRule2": ' ||
                  (SELECT coalesce(a.transfers_outside_tal, false) FROM account a WHERE t.account_id = a.id) || '}')::jsonb ||
                 ('{"currentRule3": ' ||
                  (SELECT coalesce(a.single_person_approval_required, true) FROM account a WHERE t.account_id = a.id) || '}')::jsonb
WHERE t.type = 'TRANSACTION_RULES_UPDATE_REQUEST'
  AND t.difference::json -> 'currentRule1' IS NULL
  AND t.difference::json -> 'rule1' IS NOT NULL
  AND t.status = 'SUBMITTED_NOT_YET_APPROVED';