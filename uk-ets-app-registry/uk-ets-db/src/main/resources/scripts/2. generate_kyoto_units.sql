/**
 * Creates random Kyoto units on all Kyoto accounts.
 * (returns) the number of produced unit blocks.
 */
create or replace function generate_kyoto_unit_blocks()
    returns bigint as
$body$
declare
    v_counter integer := 0;
    v_account record;
    v_number_of_blocks integer;
    v_quantity integer;
    v_start_block bigint := 0;
    v_end_block bigint := 0;
    v_type unit_block.unit_type%type;
    v_types varchar[] := array['AAU', 'RMU', 'ERU_FROM_AAU', 'ERU_FROM_RMU', 'CER', 'TCER', 'LCER'];
    v_activities varchar[] := array['AFFORESTATION_AND_REFORESTATION', 'DEFORESTATION', 'FOREST_MANAGEMENT', 'CROPLAND_MANAGEMENT', 'GRAZING_LAND_MANAGEMENT', 'REVEGETATION', 'WETLAND_DRAINAGE_AND_REWETTING'];
    v_tracks varchar[] := array['TRACK_1', 'TRACK_2'];

    v_expiry_date unit_block.expiry_date%type;
    v_project_number unit_block.project_number%type;
    v_project_track unit_block.project_track%type;
    v_activity unit_block.environmental_activity%type;
    v_commitment_period unit_block.original_period%type;
begin

    for v_account in
        -- Retrieve Kyoto accounts
        select identifier, registry_account_type
          from account
         where registry_account_type = 'NONE'
        loop
            v_number_of_blocks = random_number(1, 20);

            -- Select a random unit type
            v_type = v_types[random_number(1, array_length(v_types, 1))];

            v_expiry_date := null;
            v_project_number := null;
            v_project_track := null;
            v_activity := null;
            v_commitment_period = 'CP' || random_number(1, 2);

            if v_type = 'RMU' then
                -- Select a random environmental LULUCF activity
                v_activity = v_activities[random_number(1, array_length(v_activities, 1))];
            end if;

            if v_type = 'ERU_FROM_AAU' or v_type = 'ERU_FROM_RMU' or v_type = 'CER' or v_type = 'TCER' or v_type = 'LCER' then
                -- Select a random project
                v_project_number = 'GB' || random_number(100, 5000);
                v_project_track = v_tracks[random_number(1, array_length(v_tracks, 1))];
            end if;

            if v_type = 'TCER' or v_type = 'LCER' then
               v_expiry_date = now();
            end if;

            for counter in 1..v_number_of_blocks loop
                v_quantity = random_number(1, 1000);
                v_start_block = nextval('issuance_seq');
                v_end_block = v_start_block + v_quantity - 1;
                insert into unit_block(
                    id, start_block, end_block, unit_type, originating_country_code, original_period, applicable_period,
                    account_identifier, acquisition_date, expiry_date, project_number, project_track, environmental_activity)
                values (nextval('unit_block_seq'), v_start_block, v_end_block, v_type, 'GB', v_commitment_period, v_commitment_period,
                        v_account.identifier, now(), v_expiry_date, v_project_number, v_project_track, v_activity);

                perform setval('issuance_seq', (v_end_block + 1), false);
                v_counter = v_counter + 1;
            end loop;

        END LOOP;

    return v_counter;
end;
$body$
LANGUAGE plpgsql;

/**
 * Calculates the balance of every account.
 * (returns) the number of processed accounts.
 */
create or replace function update_account_balances()
    returns bigint as
$body$
declare
    v_counter integer := 0;
    v_account record;
    v_balance bigint;
begin

    for v_account in
        select id, identifier
        from account
        loop

            select sum(end_block - start_block + 1)
            from unit_block
            where account_identifier = v_account.identifier and unit_block.reserved_for_transaction is null
            into v_balance;

            update account set balance = v_balance where id = v_account.id;

            v_counter = v_counter + 1;
        end loop;

    return v_counter;
end;
$body$
LANGUAGE plpgsql;
