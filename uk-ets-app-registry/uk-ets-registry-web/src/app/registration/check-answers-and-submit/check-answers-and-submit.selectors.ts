import { IUkOfficialCountry } from 'src/app/shared/countries/country.interface';
import { IUser } from 'src/app/shared/user/user';
import { diffInYears, getDayjs } from '../../shared/shared.util';
import { createSelector } from '@ngrx/store';
import { State } from '@registry-web/reducers';

export const selectUser = (state: State) => state.registration.user;
export const selectCountries = (state: State) => state.shared.countries;

export const selectResidenceCountry = createSelector(
  selectUser,
  selectCountries,
  (theUser: IUser, allCountries: IUkOfficialCountry[]) => {
    if (theUser && allCountries) {
      return allCountries.find((country) => country.key === theUser.country)
        .item[0].name;
    }
  }
);

export const selectWorkCountry = createSelector(
  selectUser,
  selectCountries,
  (theUser: IUser, allCountries: IUkOfficialCountry[]) => {
    if (theUser && allCountries) {
      return allCountries.find((country) => country.key === theUser.workCountry)
        .item[0].name;
    }
  }
);

export const selectBirthCountry = createSelector(
  selectUser,
  selectCountries,
  (theUser: IUser, allCountries: IUkOfficialCountry[]) => {
    if (theUser && allCountries) {
      return allCountries.find(
        (country) => country.key === theUser.countryOfBirth
      ).item[0].name;
    }
  }
);

export const selectAge = createSelector(selectUser, (theUser: IUser) => {
  if (theUser) {
    const birthMoment = getDayjs(theUser.birthDate);
    const age = diffInYears(birthMoment);
    return age;
  }
});

export const selectPhoneNumberFull = createSelector(
  selectUser,
  (theUser: IUser) => {
    if (theUser) {
      return '+' + theUser.workCountryCode + theUser.workPhoneNumber;
    }
  }
);
