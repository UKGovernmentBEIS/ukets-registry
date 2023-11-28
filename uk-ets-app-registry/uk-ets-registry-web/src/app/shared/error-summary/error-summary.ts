import { ErrorDetail } from './error-detail';
// TODO remove ErrorSummary Class and name the interface ErrorSummary
export class ErrorSummary {
  constructor(public errors: ErrorDetail[]) {}
}

export interface IErrorSummary {
  errors: ErrorDetail[];
}
