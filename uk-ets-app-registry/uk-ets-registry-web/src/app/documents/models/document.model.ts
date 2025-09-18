export type RegistryDocument = {
  id: number;
  name: string;
  title: string;
  order: number;
  createdOn: Date;
};

// multipart
export type SaveRegistryDocumentDTO = {
  id?: number;
  title: string;
  categoryId: number;
  order: number;
  file: File;
};

export type SaveRegistryDocumenCategorytDTO = {
  id?: number;
  name: string;
  order: number;
};

export type RegistryDocumentCategory = {
  id: number;
  name: string;
  order: number;
  createdOn: Date;
  documents: RegistryDocument[];
};

export type UpdateRegistryDocumentCategoryDTO = {
  id?: number;
  name: string;
  order: number;
};

export type UpdateRegistryDocumentDTO = {
  id?: number;
  title?: string;
  name: string;
  order: number;
};
