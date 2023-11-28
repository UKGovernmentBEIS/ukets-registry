import { ErrorDetail } from '@registry-web/shared/error-summary';
import { PageParameters } from '@registry-web/shared/search/paginator';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';

export interface MessageSearchCriteria {
  messageId: number;
  messageDateFrom: string;
  messageDateTo: string;
}

export interface MessageSearchResult {
  messageId: number;
  messageDate: string;
  content: string;
  from: string;
  to: string;
}

export interface SearchActionPayload {
  criteria: MessageSearchCriteria;
  pageParameters: PageParameters;
  sortParameters: SortParameters;
  potentialErrors: Map<any, ErrorDetail>;
  loadPageParametersFromState?: boolean;
}

export interface MessageDetails {
  messageId: number;
  messageDate: string;
  content: string;
}

export interface SendMessageRequest {
  content: string;
}

export interface SendMessageResponse {
  success: boolean;
  messageId: number;
}
