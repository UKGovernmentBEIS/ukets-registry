// TODO: remove this ErrorDetail Class and replace with interface ErrorDetail
export class ErrorDetail {
  constructor(public componentId: string, public errorMessage: string) {}
}

export interface ErrorDetail {
  readonly errorId?: string;
  readonly errorFileId?: number;
  readonly errorFilename?: string;
}
