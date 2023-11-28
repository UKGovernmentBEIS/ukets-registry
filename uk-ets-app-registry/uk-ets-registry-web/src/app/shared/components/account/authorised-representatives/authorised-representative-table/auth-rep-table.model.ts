export enum AuthRepTableColumns {
  NAME = 'Name',
  USER_ID = 'User ID',
  ACCESS_RIGHTS = 'Permissions',
  WORK_CONTACT = 'Work contacts',
  AR_STATUS = 'AR status',
  USER_STATUS = 'User status',
}

export interface CustomColumn {
  columnName: string;
  columnValues: any[];
}
