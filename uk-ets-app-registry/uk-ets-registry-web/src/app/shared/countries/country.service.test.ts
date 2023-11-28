import { of } from 'rxjs';
import { CountryService } from './country.service';

const httpClient = {
  get: jest.fn()
};

describe('CountryService', () => {
  let countryService: CountryService;
  beforeAll(() => {
    httpClient.get.mockImplementationOnce(() => of(testCountries()));
    countryService = new CountryService(httpClient as any);
  });

  test('returns countries with calling code', async () => {
    expect.assertions(1);
    expect(await countryService.getCountries().toPromise()).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          key: 'US',
          callingCode: 1
        })
      ])
    );
  });
});

function testCountries() {
  return {
    GR: {
      'index-entry-number': '73',
      'entry-number': '73',
      'entry-timestamp': '2016-04-05T13:23:05Z',
      key: 'GR',
      item: [
        {
          country: 'GR',
          'official-name': 'The Hellenic Republic',
          name: 'Greece',
          'citizen-names': 'Greek'
        }
      ]
    },
    US: {
      'index-entry-number': '191',
      'entry-number': '191',
      'entry-timestamp': '2016-04-05T13:23:05Z',
      key: 'US',
      item: [
        {
          country: 'US',
          'official-name': 'The United States of America',
          name: 'United States',
          'citizen-names': 'United States citizen'
        }
      ]
    }
  };
}
