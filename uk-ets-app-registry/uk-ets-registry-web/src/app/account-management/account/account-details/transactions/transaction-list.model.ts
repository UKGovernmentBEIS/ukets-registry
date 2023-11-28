import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';

export interface SearchActionPayload {
  pageParameters: PageParameters;
  sortParameters: SortParameters;
  loadPageParametersFromState: boolean;
}
