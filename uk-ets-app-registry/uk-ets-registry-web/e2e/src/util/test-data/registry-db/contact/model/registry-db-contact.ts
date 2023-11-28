import { RegistryDbContactBuilder } from './registry-db-contact.builder';

export class RegistryDbContact {
  id: string;
  line_1: string;
  line_2: string;
  line_3: string;
  post_code: string;
  city: string;
  country: string;
  phone_number_1: string;
  phone_number_1_country: string;
  phone_number_2: string;
  phone_number_2_country: string;
  email_address: string;

  constructor(builder: RegistryDbContactBuilder) {
    this.line_1 = builder.getLine_1;
    this.line_2 = builder.getLine_2;
    this.line_3 = builder.getLine_3;
    this.post_code = builder.getPost_code;
    this.city = builder.getCity;
    this.country = builder.getCountry;
    this.phone_number_1 = builder.getPhone_number_1;
    this.phone_number_1_country = builder.getPhone_number_1_country;
    this.phone_number_2 = builder.getPhone_number_2;
    this.phone_number_2_country = builder.getPhone_number_2_country;
    this.email_address = builder.getEmail_address;
  }
}
