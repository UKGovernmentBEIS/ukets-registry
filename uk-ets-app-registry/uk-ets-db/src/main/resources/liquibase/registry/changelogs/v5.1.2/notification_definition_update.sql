INSERT INTO "notification_definition" (
    "id", "type", "short_text", "long_text", "channel_type", "supported_parameters", "selection_criteria", "type_id")
VALUES 
    (nextval('notification_definition_seq'),
     'USER_INACTIVITY', 
     '','','EMAIL',NULL,'{"accountTypes":["ETS - Operator holding account","ETS - Aircraft operator holding account","ETS - Maritime operator holding account","ETS - Trading account"],"accountStatuses":["OPEN","CLOSURE_PENDING","ALL_TRANSACTIONS_RESTRICTED","SOME_TRANSACTIONS_RESTRICTED"],"userStatuses":["ENROLLED","VALIDATED","DEACTIVATION_PENDING"],"accountAccessStates":["ACTIVE"]}', 8);