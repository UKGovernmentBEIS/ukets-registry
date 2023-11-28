create sequence registry_level_seq minvalue 0 maxvalue 999999999999999999 increment by 1 start with 1 cache 1 no cycle;

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'AAU', 'CP1', null, 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP1', 'AFFORESTATION_AND_REFORESTATION', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP1', 'DEFORESTATION', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP1', 'FOREST_MANAGEMENT', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP1', 'CROPLAND_MANAGEMENT', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP1', 'GRAZING_LAND_MANAGEMENT', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP1', 'REVEGETATION', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP1', 'WETLAND_DRAINAGE_AND_REWETTING', 0, 0, 0);


-- Commitment Period 2 / Phase III
insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'AAU', 'CP2', null, 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP2', 'AFFORESTATION_AND_REFORESTATION', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP2', 'DEFORESTATION', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP2', 'FOREST_MANAGEMENT', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP2', 'CROPLAND_MANAGEMENT', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP2', 'GRAZING_LAND_MANAGEMENT', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP2', 'REVEGETATION', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP2', 'WETLAND_DRAINAGE_AND_REWETTING', 0, 0, 0);


-- Commitment Period 3 / Phase IV
insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'AAU', 'CP3', null, 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP3', 'AFFORESTATION_AND_REFORESTATION', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP3', 'DEFORESTATION', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP3', 'FOREST_MANAGEMENT', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP3', 'CROPLAND_MANAGEMENT', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP3', 'GRAZING_LAND_MANAGEMENT', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP3', 'REVEGETATION', 0, 0, 0);

insert into registry_level(id, type, unit_type, commitment_period, environmental_activity, initial, consumed, pending)
values (nextval('registry_level_seq'), 'ISSUANCE_KYOTO_LEVEL', 'RMU', 'CP3', 'WETLAND_DRAINAGE_AND_REWETTING', 0, 0, 0);

-- test data for accounts



	insert into account
			(
			id,
			identifier,
			account_name,
			registry_account_type,
			kyoto_account_type,
			account_status,
			opening_date,
			status,
			commitment_period_code,
			registry_code,
			check_digits,
			full_identifier,
			type_label
			)
			values
			(
			nextval('account_seq'),
			1000,
			'Party Holding 1',
			'NONE',
			'PARTY_HOLDING_ACCOUNT',
			'OPEN',
			now(),
			'ACTIVE',
			1,
			'GB',
			94,
			'GB-100-1000-1-94',
			'Party Holding Account'
			);

			insert into account
			(
			id,
			identifier,
			account_name,
			registry_account_type,
			kyoto_account_type,
			account_status,
			opening_date,
			status,
			commitment_period_code,
			registry_code,
			check_digits,
			full_identifier,
			type_label
			)
			values
			(
			nextval('account_seq'),
			1001,
			'Party Holding 2',
			'NONE',
			'PARTY_HOLDING_ACCOUNT',
			'OPEN',
			now(),
			'ACTIVE',
			1,
			'GB',
			89,
			'GB-100-1001-1-89',
			'Party Holding Account'
			);

			insert into account
			(
			id,
			identifier,
			account_name,
			registry_account_type,
			kyoto_account_type,
			account_status,
			opening_date,
			status,
			commitment_period_code,
			registry_code,
			check_digits,
			full_identifier,
			type_label
			)
			values
			(
			nextval('account_seq'),
			1002,
			'Party Holding 3',
			'NONE',
			'PARTY_HOLDING_ACCOUNT',
			'OPEN',
			now(),
			'ACTIVE',
			1,
			'GB',
			84,
			'GB-100-1002-1-84',
			'Party Holding Account'
			);

			insert into account
			(
			id,
			identifier,
			account_name,
			registry_account_type,
			kyoto_account_type,
			account_status,
			opening_date,
			status,
			commitment_period_code,
			registry_code,
			check_digits,
			full_identifier,
			type_label
			)
			values
			(
			nextval('account_seq'),
			1003,
			'Party Holding 4',
			'NONE',
			'PARTY_HOLDING_ACCOUNT',
			'OPEN',
			now(),
			'ACTIVE',
			2,
			'GB',
			76,
			'GB-100-1003-2-76',
			'Party Holding Account'
			);

			insert into account
			(
			id,
			identifier,
			account_name,
			registry_account_type,
			kyoto_account_type,
			account_status,
			opening_date,
			status,
			commitment_period_code,
			registry_code,
			check_digits,
			full_identifier,
			type_label
			)
			values
			(
			nextval('account_seq'),
			1004,
			'Party Holding 5',
			'NONE',
			'PARTY_HOLDING_ACCOUNT',
			'OPEN',
			now(),
			'ACTIVE',
			2,
			'GB',
			71,
			'GB-100-1004-2-71',
			'Party Holding Account'
			);

			insert into account
			(
			id,
			identifier,
			account_name,
			registry_account_type,
			kyoto_account_type,
			account_status,
			opening_date,
			status,
			commitment_period_code,
			registry_code,
			check_digits,
			full_identifier,
			type_label
			)
			values
			(
			nextval('account_seq'),
			1005,
			'Party Holding 6',
			'NONE',
			'PARTY_HOLDING_ACCOUNT',
			'OPEN',
			now(),
			'ACTIVE',
			2,
			'GB',
			66,
			'GB-100-1005-2-66',
			'Party Holding Account'
			);

			insert into account
			(
			id,
			identifier,
			account_name,
			registry_account_type,
			kyoto_account_type,
			account_status,
			opening_date,
			status,
			commitment_period_code,
			registry_code,
			check_digits,
			full_identifier,
			type_label
			)
			values
			(
			nextval('account_seq'),
			1006,
			'Party Holding 7',
			'NONE',
			'PARTY_HOLDING_ACCOUNT',
			'OPEN',
			now(),
			'ACTIVE',
			3,
			'GB',
			58,
			'GB-100-1006-3-58',
			'Party Holding Account'
			);

			insert into account
			(
			id,
			identifier,
			account_name,
			registry_account_type,
			kyoto_account_type,
			account_status,
			opening_date,
			status,
			commitment_period_code,
			registry_code,
			check_digits,
			full_identifier,
			type_label
			)
			values
			(
			nextval('account_seq'),
			1007,
			'Party Holding 8',
			'NONE',
			'PARTY_HOLDING_ACCOUNT',
			'OPEN',
			now(),
			'ACTIVE',
			3,
			'GB',
			53,
			'GB-100-1007-3-53',
			'Party Holding Account'
			);

			insert into account
			(
			id,
			identifier,
			account_name,
			registry_account_type,
			kyoto_account_type,
			account_status,
			opening_date,
			status,
			commitment_period_code,
			registry_code,
			check_digits,
			full_identifier,
			type_label
			)
			values
			(
			nextval('account_seq'),
			1008,
			'Party Holding 9',
			'NONE',
			'PARTY_HOLDING_ACCOUNT',
			'OPEN',
			now(),
			'ACTIVE',
			3,
			'GB',
			48,
			'GB-100-1008-3-48',
			'Party Holding Account'
			);


			insert into account
			(
			id,
			identifier,
			account_name,
			registry_account_type,
			kyoto_account_type,
			account_status,
			opening_date,
			status,
			commitment_period_code,
			registry_code,
			check_digits,
			full_identifier,
			type_label
			)
			values
			(
			nextval('account_seq'),
			1011,
			'Party Holding 19',
			'NONE',
			'PARTY_HOLDING_ACCOUNT',
			'OPEN',
			now(),
			'ACTIVE',
			3,
			'GB',
			48,
			'GB-100-1008-3-48',
			'Party Holding Account'
			);
