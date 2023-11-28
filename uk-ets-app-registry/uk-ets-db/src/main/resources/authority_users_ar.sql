do
$$
    DECLARE
        authorityUsersIdList        integer ARRAY := ARRAY(select u.id
                                                           from users u
                                                                    join user_role_mapping urm on u.id = urm.user_id
                                                                    join iam_user_role iur on urm.role_id = iur.id
                                                           where iur.role_name = 'authority-user');
        accountIdList               integer ARRAY := ARRAY(select id
                                                           from account
                                                           where registry_account_type IN
                                                                 ('UK_TOTAL_QUANTITY_ACCOUNT', 'UK_AUCTION_ACCOUNT',
                                                                  'UK_NEW_ENTRANTS_RESERVE_ACCOUNT',
                                                                  'UK_MARKET_STABILITY_MECHANISM_ACCOUNT',
                                                                  'UK_GENERAL_HOLDING_ACCOUNT'));
        accountId                   integer;
        readOnlyAccessAccountIdList integer ARRAY := ARRAY(select id
                                                           from account
                                                           where registry_account_type IN ('UK_SURRENDER_ACCOUNT',
                                                                                           'UK_DELETION_ACCOUNT'));
        readOnlyAccessAccountId     integer;
        counter                     integer       := 0;

    BEGIN
        FOREACH accountId IN ARRAY accountIdList
            LOOP
                <<inner_loop1>>
                LOOP
                    EXIT inner_loop1 WHEN counter = cardinality(authorityUsersIdList);
                    counter := counter + 1;
                    insert into account_access (id, state, account_id, user_id, access_right)
                    values (nextval('account_access_seq'), 'ACTIVE', accountId,
                            authorityUsersIdList[counter], 'INITIATE_AND_APPROVE');
                END LOOP;
                counter := 0;
            END LOOP;
        FOREACH readOnlyAccessAccountId IN ARRAY readOnlyAccessAccountIdList
            LOOP
                <<inner_loop2>>
                LOOP
                    EXIT inner_loop2 WHEN counter = cardinality(authorityUsersIdList);
                    counter := counter + 1;
                    insert into account_access (id, state, account_id, user_id, access_right)
                    values (nextval('account_access_seq'), 'ACTIVE', readOnlyAccessAccountId,
                            authorityUsersIdList[counter], 'READ_ONLY');
                END LOOP;
                counter := 0;
            END LOOP;
    END;
$$
