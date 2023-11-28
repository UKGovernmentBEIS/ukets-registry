import { AuthorisedRepresentativeDetailsPipe } from './authorised-representative-details.pipe';
import { AuthorisedRepresentative } from '@shared/model/account';

describe('AuthorisedRepresentativeDetailsPipe', () => {
  const pipe = new AuthorisedRepresentativeDetailsPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform object to string with first and last name', () => {
    const ar: AuthorisedRepresentative = {
      urid: '123',
      lastName: 'Last',
      firstName: 'First',
      user: {
        alsoKnownAs: '',
      },
    } as AuthorisedRepresentative;
    expect(pipe.transform(ar)).toEqual('First Last (User ID: 123)');
  });

  it('should transform object to string with known as name', () => {
    const ar: AuthorisedRepresentative = {
      urid: '123',
      lastName: 'Last',
      firstName: 'First',
      user: {
        alsoKnownAs: 'Known As',
      },
    } as AuthorisedRepresentative;
    expect(pipe.transform(ar)).toEqual('Known As (User ID: 123)');
  });

  it('should return empty string if null', () => {
    expect(pipe.transform(null)).toEqual('');
    expect(pipe.transform(undefined)).toEqual('');
  });
});
