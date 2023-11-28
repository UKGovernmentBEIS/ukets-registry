-- INSERT USERS
insert into users (id, urid, iam_identifier, enrolment_key, enrolment_key_date, state, first_name, last_name,
                   disclosed_name, previous_state, known_as)
values (1, 'UK405681794859', 'a449d452-5af1-4ee4-9db4-c08bdcf7c0d4', null, null, 'ENROLLED', 'Representative 1',
        'Authorized', 'UK ETS Authorized Representative 1', null, 'UK ETS Authorized Representative 1'),
       (2, 'UK588332110438', '532a4a3a-c8f0-4f0f-a13a-54f40ead85ab', null, null, 'ENROLLED', 'Representative 2',
        'Authorized', 'UK ETS Authorized Representative 2', null, 'UK ETS Authorized Representative 2'),
       (12, 'UK802061511788', '59d51968-1fd2-4150-8d3b-15abba3a4b21', null, null, 'ENROLLED', 'Registry Administrator',
        'Senior', 'Registry Administrator', null, 'UK ETS Senior Registry Administrator'),
       (14, 'UK600543316240', 'b3ad38e3-62b9-4e59-b162-8424a46eb865', null, null, 'ENROLLED',
        'Representative 3', 'Authorized', 'UK ETS Authorized Representative 3', null, 'Representative 3'),
       (15, 'UK661201404082', '2ca5bb26-563b-46f5-9c62-287dd4c3da51', null, null, 'ENROLLED',
        'Representative 4', 'Authorized', 'UK ETS Authorized Representative 4', null, 'Representative 4');

-- INSERT COMPLIANT ENTITIES
insert into compliant_entity (id, identifier, status, account_id, start_year, end_year, has_been_compliant, regulator,
                              changed_regulator, allocation_classification, allocation_withhold_status)
values (1, 1000000, null, null, 2021, null, null, 'EA', null, null, null),
       (2, 1000001, null, null, 2021, null, null, 'NRW', null, null, null);

-- INSERT CONTACTS
insert into contact (id, line_1, line_2, line_3, post_code, city, country, phone_number_1, phone_number_2,
                            email_address, phone_number_1_country, phone_number_2_country, position_in_company)
values (1, '1 Victoria Street', '', '', 'SW1H 0ET', 'London', 'UK', '02072155000', null, 'enquiries@beis.gov.uk', 'UK',
        null, null),
       (2, 'Kyoto Gov Account Holder Contact', '', '', 'SW1H 0ET', 'London', 'UK', '02072155000', null,
        'enquiries@beis.gov.uk', 'UK', null, null),
       (3, '', '', '', '', '', 'UK', null, null, null, null, null, null),
       (4, '1 Baker Street', '', '', 'N32TT', 'London', 'UK', null, null, null, null, null, null),
       (5, '1 Taylor Street', '', '', 'N32SS', 'London', 'UK', '07756780987', '', 'contact1@trasys.gr', '44', '', null),
       (6, '', '', '', '', '', 'UK', null, null, null, null, null, null),
       (7, 'Chadwick Street', '', '', 'BT97FD', 'Belfast', 'UK', null, null, null, null, null, null),
       (8, 'Chadwick Street', '', '', 'BT97FD', 'Belfast', 'UK', '07756780987', '', 'contact1@trasys.gr', '44', '',
        null),
       (9, 'Ethnikis Antistasis 30', '', '', '', 'Athens', 'GR', null, null, null, null, null, null),
       (10, 'Ethnikis Antistasis 30', '', '', '', 'Athens', 'GR', null, null, null, null, null, null),
       (11, 'Carnaby', '', '', 'N32TT', 'London', 'UK', '07756780987', '', 'contact1@trasys.gr', '44', '', null),
       (12, 'Sutherland Str 16', '', '', 'SW1V 4LA', 'London', 'UK', null, null, null, null, null, null),
       (13, 'Sutherland Str', '', '', 'SW1V 4LA', 'London', 'UK', '2075968755', '', 'pha1@trasys.gr', '44', '', null),
       (14, 'Clarendon Str 5', '', '', 'SW1V 4QT', 'London', 'UK', '20 7588 8750', '', 'contact1@trasys.gr', '44', '',
        null),
       (15, 'Ebury Str 88', '', '', 'SW1W 9QB', 'London', 'UK', null, null, null, null, null, null),
       (16, 'Cambridge St 15', '', '', 'SW1V 4LA', 'London', 'UK', '2075922754', '', 'pha2@trasys.gr', '44', '', null),
       (17, 'Gloucester St 9', '', '', 'SW7 4AU', 'London', 'UK', '20 7511 8744', '', 'contact2@trasys.gr', '44', '',
        null),
       (23, 'ΝΕΡΟΥΤΣΟΥ', '12', 'test', '10445', 'ΑΘΗΝΑ', 'UK', null, null, null, null, null, null),
       (24, 'ΝΕΡΟΥΤΣΟΥ', '12', 'test', '10445', 'ΑΘΗΝΑ', 'GR', null, null, null, null, null, null);

-- INSERT ACCOUNT HOLDERS
insert into account_holder (id, identifier, name, birth_country, registration_number, type,
                            contact_id, birth_date, first_name, last_name)
values (1, 1000000, 'UK ETS Authority', null, null,  'GOVERNMENT', 1, null, null, null),
       (4, 1000003, 'ABC Limited', null, 'Test', 'ORGANISATION', 4, null, null, null),
       (5, 1000004, 'British Wings', null, '', 'ORGANISATION', 7, null, null, null);

-- INSERT ACCOUNTS
insert into account (id, identifier, account_name, registry_account_type, kyoto_account_type, account_status,
                     opening_date, request_status, account_holder_id, commitment_period_code, registry_code,
                     compliant_entity_id, check_digits, transfers_outside_tal, approval_second_ar_required,
                     billing_address_same_as_account_holder_address, contact_id, compliance_status, balance,
                     full_identifier, type_label, unit_type, closing_date, single_person_approval_required)
values
       (50, 10000060, 'New Account 1', 'TRADING_ACCOUNT', 'PARTY_HOLDING_ACCOUNT', 'OPEN', '2022-01-12 18:48:45.953000',
        'ACTIVE', 4, 0, 'UK', null, 52, false, false, null, 23, null, 0, 'UK-100-10000060-0-52',
        'ETS - Trading account', null, null, true),
       (51, 10000061, 'New Account 2', 'TRADING_ACCOUNT', 'PARTY_HOLDING_ACCOUNT', 'OPEN', '2022-01-12 19:01:18.893000',
        'ACTIVE', 5, 0, 'UK', null, 47, false, true, null, 24, null, 0, 'UK-100-10000061-0-47', 'ETS - Trading account',
        null, null, true);

insert into account_access (id, state, account_id, user_id, access_right)
values

       --Representative 1
        (10, 'ACTIVE', 51, 1, 'INITIATE'),
        (11, 'ACTIVE', 50, 1, 'INITIATE'),

       --Representative 2
        (20, 'ACTIVE', 51, 2, 'INITIATE'),

       -- Representative 14
        (30, 'ACTIVE', 50, 14, 'INITIATE_AND_APPROVE'),

        -- Representative 15
        (40, 'ACTIVE', 51, 15, 'INITIATE_AND_APPROVE'),

       --SRA id = 12 Accesses
        (86, 'ACTIVE', 50, 12, 'ROLE_BASED'),
        (96, 'ACTIVE', 51, 12, 'ROLE_BASED')
;