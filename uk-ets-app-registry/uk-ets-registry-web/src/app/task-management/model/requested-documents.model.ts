export interface RequestedDocumentsModel {
  totalFileUploads?: RequestedDocumentsModel[];
  file?: File;
  documentName?: string;
  index: number;
  id: number;
}
