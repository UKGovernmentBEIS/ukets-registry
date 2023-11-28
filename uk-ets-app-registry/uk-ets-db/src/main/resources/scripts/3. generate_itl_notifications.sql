
/**
 * Creates random Kyoto units on all Kyoto accounts.
 * (returns) the number of produced unit blocks.
 */
create or replace function generate_itl_notifications()
    returns bigint as
$body$
declare
    v_counter integer := 0;
    v_identifier bigint := 0;
    v_target_date itl_notification_history.target_date%type;
    v_project_count integer;
    v_quantity integer;
    v_start_block bigint := 0;
    v_end_block bigint := 0;

    v_type itl_notification_history.type%type;

    v_types varchar[] := array[
        'NET_SOURCE_CANCELLATION',
        'NON_COMPLIANCE_CANCELLATION',
        'IMPENDING_EXPIRY_OF_TCER_AND_LCER',
        'REVERSAL_OF_STORAGE_FOR_CDM_PROJECT',
        'NON_SUBMISSION_OF_CERTIFICATION_REPORT_FOR_CDM_PROJECT',
        'EXCESS_ISSUANCE_FOR_CDM_PROJECT',
        'EXPIRY_DATE_CHANGE',
        'NET_REVERSAL_OF_STORAGE_OF_A_CDM_CCS_PROJECT',
        'NON_SUBMISSION_OF_VERIFICATION_REPORT_FOR_A_CDM_CCS_PROJECT'];

    v_activities varchar[] := array['AFFORESTATION_AND_REFORESTATION', 'DEFORESTATION', 'FOREST_MANAGEMENT', 'CROPLAND_MANAGEMENT', 'GRAZING_LAND_MANAGEMENT', 'REVEGETATION', 'WETLAND_DRAINAGE_AND_REWETTING'];
    v_tracks varchar[] := array['TRACK_1', 'TRACK_2'];

    v_unit_types varchar[] := array['TCER', 'LCER'];

    v_unit_type itl_notification_history.unit_type%type;
    v_project_number itl_notification_history.project_number%type;
    v_project_track unit_block.project_track%type;
    v_activity itl_notification_history.environmental_activity%type;
    v_commitment_period unit_block.original_period%type;

    v_not_id itl_notification.id%type;
    v_his_id itl_notification_history.id%type;


begin

    FOREACH v_type IN ARRAY v_types
        LOOP
            select nextval('account_identifier_seq') into v_identifier;
            select nextval('itl_notification_seq') into v_not_id;
            select nextval('itl_notification_history_seq') into v_his_id;

            v_quantity = random_number(1, 50);
            v_commitment_period = null;
            v_activity = null;
            v_project_number = null;
            v_unit_type = null;
            v_target_date = null;
            v_project_count = 0;

            -- Specify environmental LULUCF activity
            if v_type = 'NET_SOURCE_CANCELLATION' then
                v_activity = v_activities[random_number(1, array_length(v_activities, 1))];
            end if;

            -- Specify unit type
            if v_type = 'EXCESS_ISSUANCE_FOR_CDM_PROJECT' or v_type = 'EXPIRY_DATE_CHANGE' then
                v_unit_type = v_unit_types[random_number(1, array_length(v_unit_types, 1))];
            end if;

            -- Specify commitment period
            if v_type = 'NET_SOURCE_CANCELLATION' or v_type = 'NON_COMPLIANCE_CANCELLATION' then
                v_commitment_period = 'CP' || random_number(1, 2);
            end if;

            if v_type = 'EXPIRY_DATE_CHANGE' and v_unit_type = 'TCER' then
                v_commitment_period = 'CP' || random_number(1, 2);
            end if;

            -- Specify target date
            if v_type = 'EXPIRY_DATE_CHANGE' then
                v_target_date = now();
            end if;

            -- Specify project
            if v_type = 'REVERSAL_OF_STORAGE_FOR_CDM_PROJECT' or
               v_type = 'NON_SUBMISSION_OF_CERTIFICATION_REPORT_FOR_CDM_PROJECT' or
               v_type = 'EXCESS_ISSUANCE_FOR_CDM_PROJECT' or
               v_type = 'EXPIRY_DATE_CHANGE' or
               v_type = 'NET_REVERSAL_OF_STORAGE_OF_A_CDM_CCS_PROJECT' or
               v_type = 'NON_SUBMISSION_OF_VERIFICATION_REPORT_FOR_A_CDM_CCS_PROJECT' then

                select count(project_number)
                from unit_block u
                join account a on u.account_identifier = a.identifier
                where a.registry_account_type = 'NONE'
                and u.project_number is not null
                and u.reserved_for_transaction is null
                into v_project_count;

                select project_number
                from unit_block u
                join account a on u.account_identifier = a.identifier
                where a.registry_account_type = 'NONE'
                and u.project_number is not null
                and u.reserved_for_transaction is null
                offset floor(random() * v_project_count)
                limit 1
                into v_project_number;

                if v_unit_type is not null then
                    select count(project_number)
                    from unit_block u
                    join account a on u.account_identifier = a.identifier
                    where a.registry_account_type = 'NONE'
                    and u.project_number is not null
                    and u.reserved_for_transaction is null
                    and u.unit_type = v_unit_type
                    into v_project_count;

                    select project_number
                    from unit_block u
                    join account a on u.account_identifier = a.identifier
                    where a.registry_account_type = 'NONE'
                    and u.project_number is not null
                    and u.reserved_for_transaction is null
                    and u.unit_type = v_unit_type
                    offset floor(random() * v_project_count)
                    limit 1
                    into v_project_number;
                end if;

            end if;

            insert into itl_notification (id, identifier, type, status)
            values (v_not_id, v_identifier, v_type, 'OPEN');

            insert into itl_notification_history
                (id, message_date, message_content, type, status, project_number,
                 unit_type, target_value, target_date, environmental_activity,
                 commitment_period, action_due_date, notice_log_id)
            values (v_his_id, now(), random_string(20),
                        v_type, 'OPEN', v_project_number, v_unit_type, v_quantity, v_target_date, v_activity,
                        v_commitment_period, now(), v_not_id);

            v_counter = v_counter + 1;

        END LOOP;

    return v_counter;
end;
$body$
    LANGUAGE plpgsql;