type SummaryListStyle =
  | ''
  | 'summary-list-change-notification'
  | 'summary-list-change-header-font-weight'
  | 'govuk-heading-m'
  | 'govuk-summary-list__value govuk-summary-list__value_change_description'
  | 'focused-text';

export interface SummaryListInfo {
  label: string;
  url?: string;
  class?: SummaryListStyle | any; // TODO: Find a workaround for uk-tag class binding in summary list values.
  innerStyle?: SummaryListStyle | any;
}

export interface SummaryListActionInfo extends SummaryListInfo {
  visuallyHidden: string;
  visible: boolean;
  clickEvent?: any;
  class?: string;
}

export interface SummaryListItem {
  key: SummaryListInfo;
  value: SummaryListInfo | SummaryListInfo[];
  action?: SummaryListActionInfo;
  projection?: string;
}
