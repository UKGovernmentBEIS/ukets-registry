export interface AccountsActionResult {
  governmentAccountsCreated: number;
  sampleAccountsCreated: number;
}

export interface UsersActionResult {
  usersDeleted: number;
  usersCreated: number;
}

export interface ResetDatabaseResult {
  accountsResult: AccountsActionResult;
  usersResult: UsersActionResult;
}
