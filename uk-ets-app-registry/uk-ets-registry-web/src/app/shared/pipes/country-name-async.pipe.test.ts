import { TestBed } from '@angular/core/testing';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { CountryNamePipe } from './country-name.pipe';
import { CountryNameAsyncPipe } from './country-name-async.pipe';
import { IUkOfficialCountry } from '../countries/country.interface';

describe('CountryNameAsyncPipe', () => {
  let pipe: CountryNameAsyncPipe;
  let store: Store;
  let countryNamePipe: CountryNamePipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({})],
      providers: [CountryNamePipe, CountryNameAsyncPipe], // Provide CountryNameAsyncPipe here
    });

    pipe = TestBed.inject(CountryNameAsyncPipe);
    store = TestBed.inject(Store);
    countryNamePipe = TestBed.inject(CountryNamePipe);
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return null when countryCode is not provided', () => {
    const result = pipe.transform(null);
    result.subscribe((_result) => {
      expect(_result).toEqual(null);
    });
  });

  it('should call countryNamePipe.transform with the correct parameters', () => {
    const mockCountry: IUkOfficialCountry = {
      'index-entry-number': '',
      'entry-number': '',
      'entry-timestamp': '',
      key: 'GR',
      item: [
        {
          country: 'GR',
          'end-date': '',
          'official-name': '',
          name: 'Greece',
          'citizen-names': '',
        },
      ],
      callingCode: '',
    };
    const mockCountries = [mockCountry];
    jest.spyOn(store, 'select').mockReturnValue(of(mockCountries));
    jest.spyOn(countryNamePipe, 'transform');

    pipe.transform('GR').subscribe((result) => {
      expect(countryNamePipe.transform).toHaveBeenCalledWith(
        'GR',
        mockCountries
      );
      expect(result).toEqual('Greece');
    });
  });
});
