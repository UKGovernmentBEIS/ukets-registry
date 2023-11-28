import { GovukTagColor } from '@shared/govuk-components/govuk-tag';

export interface Status {
  label: string;
  color: GovukTagColor;
  summary?: string;
}
