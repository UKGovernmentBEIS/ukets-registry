do
$$
    DECLARE
        adminUsersIdList integer ARRAY := ARRAY(select u.id
                                                from users u
                                                         join user_role_mapping urm on u.id = urm.user_id
                                                         join iam_user_role iur on urm.role_id = iur.id
                                                where iur.role_name in('junior-registry-administrator',
                                                                       'readonly-administrator',
                                                                       'senior-registry-administrator'));
        accountIdList    integer ARRAY := ARRAY(select id
                                                from account
                                                where registry_account_type NOT IN
                                                      ('UK_TOTAL_QUANTITY_ACCOUNT',
                                                       'UK_AUCTION_ACCOUNT',
                                                       'UK_ALLOCATION_ACCOUNT',
                                                       'UK_NEW_ENTRANTS_RESERVE_ACCOUNT',
                                                       'UK_MARKET_STABILITY_RESERVE_ACCOUNT',
                                                       'UK_GENERAL_HOLDING_ACCOUNT'));
        accountId        integer;
        counter          integer       := 0;

    BEGIN
        FOREACH accountId IN ARRAY accountIdList
            LOOP

                LOOP
                    EXIT WHEN counter = cardinality(adminUsersIdList);
                    counter := counter + 1;
                    insert into account_access (id, state, account_id, user_id, access_right)
                    values (nextval('account_access_seq'), 'ACTIVE', accountId,
                            adminUsersIdList[counter], 'ROLE_BASED');
                END LOOP;
                counter := 0;
            END LOOP;
    END;
$$