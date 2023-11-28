export interface DomainEvent {
  domainType: string;
  domainId: string;
  domainAction: string;
  description: string;
  creator: string;
  creatorType: string;
  creationDate: Date;
  creatorUserIdentifier?: string;
}
