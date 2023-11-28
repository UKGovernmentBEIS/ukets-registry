do
$$
    DECLARE
        creator       varchar;
        lastUpdated   timestamp;
        adHocId       bigint;
        startDateTime timestamp;

    BEGIN
        -- this is a randomly selected SRA
        creator = (select u.urid
                   from users u
                            inner join user_role_mapping urm on u.id = urm.user_id
                            inner join iam_user_role iur on iur.id = urm.role_id
                   where iur.role_name = 'senior-registry-administrator'
                   limit 1);

        -- creator is NULL in edge case where there are no SRAs in database (e.g. new prod-like environment)
        if (creator IS NOT NULL) THEN

            lastUpdated = (SELECT CURRENT_TIMESTAMP);

            adHocId = (select id from notification_definition where type = 'AD_HOC');
            -- this is when the initial notification banner was deployed in prod
            startDateTime = '2021-04-01'::timestamp;

            insert into notification (id, notification_definition_id, status, short_text, long_text, times_fired,
                                      creator,
                                      last_execution_date, start_date_time, end_date_time, run_every_x_days,
                                      last_updated,
                                      updated_by)

            VALUES (nextval('notification_seq'),
                    adHocId,
                    'ACTIVE',
                    'Help and Advice',
                    '<p>UK Registry help and advice</p><p>Follow the ''Guidance'' link at the bottom of the page for guidance on using the Registry.</p><p><br></p><p>If you need further advice or information you can <a href="mailto:ETRegistryHELP@environment-agency.gov.uk" rel="noopener noreferrer" target="_blank">contact the UK Registry helpdesk</a>.</p>',
                    null,
                    creator,
                    null,
                    startDateTime,
                    null, null, lastUpdated, creator);
        END IF;
    END
$$;
