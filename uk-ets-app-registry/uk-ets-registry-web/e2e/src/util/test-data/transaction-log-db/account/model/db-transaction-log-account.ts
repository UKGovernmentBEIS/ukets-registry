import { DbTransactionLogAccountBuilder } from './db-transaction-log-account.builder';

export class DbTransactionLogAccount {
  id: string;
  identifier: string;
  account_name: string;
  commitment_period_code: string;
  full_identifier: string;
  check_digits: string;
  opening_date: string;

  constructor(builder: DbTransactionLogAccountBuilder) {
    this.id = builder.getAccount_id;
    this.identifier = builder.getAccount_identifier;
    this.account_name = builder.getAccount_account_name;
    this.commitment_period_code = builder.getAccount_commitment_period_code;
    this.full_identifier = builder.getAccount_full_identifier;
    this.check_digits = builder.getAccount_check_digits;
    this.opening_date = builder.getAccount_opening_date;
  }
}
