import { TemplateRef } from '@angular/core';

export interface FormRadioOption {
  label: string;
  hint?: string;
  value: any;
  enabled: boolean;
  isHidden?: boolean;
  showExtraInputField?: boolean;
  order?: number;
  infoTemplate?: TemplateRef<any>;
}

export interface FormRadioSubGroup {
  options: FormRadioOption[];
  heading: string;
}

export interface FormRadioGroupInfo {
  radioGroupHeading: string;
  radioGroupHeadingCaption: string;
  radioGroupHint?: string;
  radioGroupSubtitle?: string;
  key: string;
  options?: FormRadioOption[];
  radioGroupSubHeading?: string;
  subGroups?: FormRadioSubGroup[];
  extraInputField?: string;
}
