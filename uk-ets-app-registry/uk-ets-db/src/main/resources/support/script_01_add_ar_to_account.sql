-- User has to enter the user identifier (urid), account full identifier and the type of the account/user access relationship.
-- Input parameters should be enclosed in single quotes.
do
$$
    DECLARE
        -- A valid user URID input format is: 'UK758208119144'
        userURID          varchar = :user_urid;

        -- A valid account full identifier format is: 'UK-100-10000034-2-79'
        accountId         integer := (select id
                                      from account
                                      where full_identifier = :account_full_identifier);

        -- Valid access rights values are: 'READ_ONLY'
        --                                 'APPROVE'
        --                                 'INITIATE'
        --                                 'INITIATE_AND_APPROVE'
        accessRights      varchar = :access_right;
        
        accountIdentifier varchar := (select account_name
                                      from account
                                      where id = accountId);
        userDisclosedName varchar := (select disclosed_name
                                      from users
                                      where urid = userURID);
    BEGIN
        IF NOT EXISTS(select aa.id
                      from account_access aa
                               inner join users on aa.user_id = users.id
                      where urid = userURID
                        and account_id = accountId) THEN
            IF accountIdentifier is not null AND userDisclosedName is not null
                AND accessRights IN ('INITIATE_AND_APPROVE', 'APPROVE',
                                     'INITIATE',
                                     'READ_ONLY')
            THEN
                RAISE INFO 'Account name of the account to associate with user: %', accountIdentifier;
                RAISE INFO 'The user that will be associated as AR for the account: %', userDisclosedName;
                RAISE INFO 'The access rights that will take place for the user/account association: %', accessRights;
                INSERT INTO account_access (id, state, account_id, user_id, access_right)
                VALUES (nextval('account_access_seq'), 'ACTIVE', accountId,
                        (select id from users where urid = userURID),
                        accessRights);
            ELSE
                RAISE EXCEPTION 'Account and/or user does not exist or access rights value is wrong';
            END IF;
        ELSE
            RAISE EXCEPTION 'User/account association already exists';
        END IF;
    END;
$$;