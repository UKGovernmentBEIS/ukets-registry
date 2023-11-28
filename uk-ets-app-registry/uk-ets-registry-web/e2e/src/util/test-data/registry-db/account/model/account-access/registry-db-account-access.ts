import { RegistryDbAccountAccessBuilder } from './registry-db-account-access.builder';

export class RegistryDbAccountAccess {
  id: string;
  state: string;
  account_id: string;
  user_id: string;
  type: string;
  access_right: string;

  constructor(builder: RegistryDbAccountAccessBuilder) {
    this.state = builder.getState();
    this.account_id = builder.getAccount_id();
    this.user_id = builder.getUser_id();
    this.type = builder.getType();
    this.access_right = builder.getAccessRight();
  }
}
