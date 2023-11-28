export type Note = {
  id: number;
  domainType: NoteType;
  domainId: string;
  description: string;
  userName: string;
  userIdentifier: string;
  creationDate: Date;
};

export type CreateNoteDTO = {
  domainType: NoteType | string;
  domainId: string;
  description: string;
};

export enum NoteType {
  ACCOUNT,
  ACCOUNT_HOLDER,
}

export const NoteTypeLabel = {
  [NoteType.ACCOUNT]: 'Account',
  [NoteType.ACCOUNT_HOLDER]: 'Account Holder',
};
