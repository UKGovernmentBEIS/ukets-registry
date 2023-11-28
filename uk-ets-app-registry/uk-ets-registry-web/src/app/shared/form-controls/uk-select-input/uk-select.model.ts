export interface Option {
  label: string;
  value: any;
}

export interface SelectableOption extends Option {
  selected?: boolean;
}
