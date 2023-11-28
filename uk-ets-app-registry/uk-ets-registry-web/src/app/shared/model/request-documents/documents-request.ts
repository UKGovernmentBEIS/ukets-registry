import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';

export interface DocumentsRequest {
  type: DocumentsRequestType;
  documentNames: string[];
  accountHolderIdentifier: string;
  accountFullIdentifier?: string;
  recipientUrid: string;
  comment: string;
  parentRequestId: string;
}
