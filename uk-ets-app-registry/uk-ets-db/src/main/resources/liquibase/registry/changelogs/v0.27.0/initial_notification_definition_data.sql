INSERT INTO notification_definition (id, type, short_text, long_text, channel_type, supported_parameters,
                                     selection_criteria)
VALUES (nextval('notification_definition_seq'), 'EMISSIONS_MISSING_FOR_OHA', 'Test Subject',
        'Test <p><strong>Test content</strong></p>', 'DASHBOARD', null,
        null),
       (nextval('notification_definition_seq'), 'SURRENDER_DEFICIT_FOR_OHA', 'Test Subject',
        'Test <p><strong>Test content</strong></p>', 'DASHBOARD', null,
        null),
       (nextval('notification_definition_seq'), 'EMISSIONS_MISSING_FOR_AOHA', 'Test Subject',
        'Test <p><strong>Test content</strong></p>', 'DASHBOARD', null,
        null),
       (nextval('notification_definition_seq'), 'SURRENDER_DEFICIT_FOR_AOHA', 'Test Subject',
        'Test <p><strong>Test content</strong></p>', 'DASHBOARD', null,
        null),
       (nextval('notification_definition_seq'), 'YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS', 'Test Subject',
        'Test <p><strong>Test content</strong></p>', 'DASHBOARD', null, null),
       (nextval('notification_definition_seq'), 'AD_HOC', null, null, 'EMAIL', null, null);
