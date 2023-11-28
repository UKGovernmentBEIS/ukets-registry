import { ConfigurationService } from './configuration.service';
import { of } from 'rxjs';

const httpClient = {
  get: jest.fn()
};

describe('ConfigurationService', () => {
  let configurationService: ConfigurationService;
  beforeAll(() => {
    httpClient.get.mockImplementationOnce(() => of(testConfiguration()));
    configurationService = new ConfigurationService(httpClient as any, '', '');
  });

  test('returns application properties', async () => {
    expect.assertions(1);
    expect(await configurationService.getConfiguration('').toPromise()).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          'spring.datasource.hikari.connectionTimeout': '30000'
        })
      ])
    );
  });
});

function testConfiguration() {
  return {
    'spring.datasource.hikari.connectionTimeout': '30000',
    'spring.datasource.hikari.maximumPoolSize': '20',
    'spring.datasource.url': 'jdbc:postgresql://localhost:5433/registry'
  };
}
