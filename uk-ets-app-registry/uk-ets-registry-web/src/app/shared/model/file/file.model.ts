export interface FileBase {
  id: number;
  fileName: string;
  fileSize: string;
  baseType?: BaseType;
  year?: number;
  sectionId?: number;
  createdOn?: Date;
}

export enum BaseType {
  ALLOCATION_TABLE = 'ALLOCATION_TABLE',
  BULK_AR = 'BULK_AR',
  EMISSIONS_TABLE = 'EMISSIONS_TABLE',
  DOCUMENT_REQUEST = 'DOCUMENT_REQUEST',
  PUBLICATION_REPORT = 'PUBLICATION_REPORT',
  AD_HOC_EMAIL_RECIPIENTS = 'AD_HOC_EMAIL_RECIPIENTS',
}

export interface NotificationsFile extends FileBase {
  tentativeRecipients?: number;
}
