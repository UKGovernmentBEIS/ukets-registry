export interface Contact {
  city: string;
  country: string;
  emailAddress: string;
  line1: string;
  line2: string;
  line3: string;
  postCode: string;
  mobilePhoneNumber?: string;
  mobileCountryCode?: string;
  alternativeCountryCode?: string;
  alternativePhoneNumber?: string;
  noMobilePhoneNumberReason?: string;
}
