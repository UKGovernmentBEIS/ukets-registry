import { User, IUser } from './user';

function aTestUser(): User {
  const user = new User();

  user.emailAddress = 'spidy@avengers.com';
  user.emailAddressConfirmation = '';
  user.userId = '8989898 8989989 8988889 9999';
  user.username = 'spidy';
  user.password = '';
  user.firstName = 'Peter';
  user.lastName = 'Parker';
  user.alsoKnownAs = 'Spiderman';
  user.buildingAndStreet = 'Somewhere in new york';
  user.buildingAndStreetOptional = '';
  user.buildingAndStreetOptional2 = '';
  user.postCode = '1111';
  user.townOrCity = 'New York';
  user.stateOrProvince = 'New York State';
  user.country = 'USA';
  user.birthDate = { day: '02', month: '03', year: '1980' };
  user.countryOfBirth = '';
  user.workCountryCode = 'USA';
  user.workPhoneNumber = '132323232323';
  user.workEmailAddress = 'spidy@avengers.com';
  user.workEmailAddressConfirmation = 'spidy@avengers.com';
  user.workBuildingAndStreet = 'Somewhere in new york';
  user.workBuildingAndStreetOptional = '';
  user.workBuildingAndStreetOptional2 = '';
  user.workTownOrCity = 'New York';
  user.workStateOrProvince = 'New York State';
  user.workPostCode = '1111';
  user.workCountry = 'USA';
  user.urid = 'GB12345678912';
  user.state = 'REGISTERED';
  user.status = 'REGISTERED';
  user.memorablePhrase = 'I love ice cream';
  return user;
}

const userObject: IUser = {
  emailAddress: 'spidy@avengers.com',
  emailAddressConfirmation: '',
  userId: '8989898 8989989 8988889 9999',
  username: 'spidy',
  password: '',
  firstName: 'Peter',
  lastName: 'Parker',
  alsoKnownAs: 'Spiderman',
  buildingAndStreet: 'Somewhere in new york',
  buildingAndStreetOptional: '',
  buildingAndStreetOptional2: '',
  postCode: '1111',
  townOrCity: 'New York',
  stateOrProvince: 'New York State',
  country: 'USA',
  birthDate: { day: '02', month: '03', year: '1980' },
  countryOfBirth: '',
  workCountryCode: 'USA',
  workPhoneNumber: '132323232323',
  workEmailAddress: 'spidy@avengers.com',
  workEmailAddressConfirmation: 'spidy@avengers.com',
  workBuildingAndStreet: 'Somewhere in new york',
  workBuildingAndStreetOptional: '',
  workBuildingAndStreetOptional2: '',
  workTownOrCity: 'New York',
  workStateOrProvince: 'New York State',
  workPostCode: '1111',
  workCountry: 'USA',
  urid: 'GB12345678912',
  state: 'REGISTERED',
  status: 'REGISTERED',
  memorablePhrase: 'I love ice cream',
};

describe('Model: User in Registration', () => {
  let theUser: User;
  beforeEach(() => {
    theUser = aTestUser();
  });

  test('a User can be created from a IUser object', () => {
    expect(User.fromJSON(userObject)).toBeDefined();
    expect(User.fromJSON(userObject)).toEqual(theUser);
  });

  test('partial updates work', () => {
    expect(
      User.updatePartially(theUser, { firstName: 'Bruce' }).firstName
    ).toEqual('Bruce');
  });

  test('decodes work', () => {
    expect(User.decode({ password: '12345678' }).password).toEqual('12345678');
  });
});
