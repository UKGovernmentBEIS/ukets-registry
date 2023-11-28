export interface IUkOfficialCountry {
  'index-entry-number': string;
  'entry-number': string;
  'entry-timestamp': string;
  key: string;
  item: [
    {
      country: string;
      'end-date': string;
      'official-name': string;
      name: string;
      'citizen-names': string;
    }
  ];
  callingCode: string;
}
