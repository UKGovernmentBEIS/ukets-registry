SELECT
  identifier,
  'GB' as registry_code,
  TO_CHAR(nh.created_date,'YYYY') AS YEAR,
  n.type,
  SUM(nh.target_value) AS amount
FROM itl_notification n
LEFT OUTER JOIN itl_notification_history nh ON n.id = nh.notice_log_id
  WHERE nh.created_date < ?
  AND n.type IN ('REVERSAL_OF_STORAGE_FOR_CDM_PROJECT','NON_SUBMISSION_OF_CERTIFICATION_REPORT_FOR_CDM_PROJECT')
GROUP BY
  n.id,
  n.identifier,
  nh.created_date,
  n.type
HAVING nh.created_date = (SELECT MAX(ce.created_date) FROM itl_notification_history ce WHERE n.id = ce.notice_log_id)