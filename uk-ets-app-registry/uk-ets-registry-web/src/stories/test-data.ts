import { FormControl, FormGroup } from '@angular/forms';
import {
  AccountAccessState,
  ARAccessRights,
  AuthorisedRepresentative
} from '@shared/model/account';
import { aTestUser } from '../app/registration/registration.test.helper';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';

/**
 * We need a new FormGroup for each story
 */
export function arFormGroupData() {
  return new FormGroup(
    {
      selectAr: new FormControl('')
    },
    { updateOn: 'submit' }
  );
}

export const authorisedRepresentativesData: AuthorisedRepresentative[] = [
  {
    urid: '111-111-111-111',
    firstName: 'first 1',
    lastName: 'last 1',
    right: ARAccessRights.INITIATE_AND_APPROVE,
    state: 'ACTIVE',
    user: aTestUser(),
    contact: null
  }
];

export const formRadioGroupInfo: FormRadioGroupInfo = {
  radioGroupHeadingCaption: 'Request allocation of UK allowances',
  radioGroupHeading: 'Select allocation year',
  radioGroupHint: 'Select one',
  options: [
    { label: '2021', value: 2021, enabled: true },
    { label: '2022', value: 2022, enabled: true }
  ],
  key: 'allocationYear'
};
