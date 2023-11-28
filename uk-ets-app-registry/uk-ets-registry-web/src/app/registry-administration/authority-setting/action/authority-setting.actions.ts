import { createAction, props } from '@ngrx/store';
import { EnrolledUser } from '@registry-web/registry-administration/authority-setting/model';

export const startAuthoritySettingWizard = createAction(
  `[Registry administration] Click on Start wizard button`
);

export const fetchEnrolledUser = createAction(
  '[Authority users settings Add user ID form on submit] Fetch enrolled user',
  props<{
    urid: string;
  }>()
);

export const fetchEnrolledUserSuccess = createAction(
  '[Authority users settings user Effect after fetching success] Load enrolled user',
  props<{
    enrolledUser: EnrolledUser;
  }>()
);

export const setUserAsAuthority = createAction(
  '[Authority users settings users Check answers page] Click on set user as authority button',
  props<{
    urid: string;
  }>()
);

export const setUserAsAuthoritySuccess = createAction(
  `[Authority users settings Effect] Set user as Authority success`
);

export const removeUserFromAuthorityUsers = createAction(
  '[Authority users settings Check answers] Click on Remove User from Authority users action button',
  props<{
    urid: string;
  }>()
);

export const removeUserFromAuthorityUsersSuccess = createAction(
  `[Authority users settings Effect] Remove User from Authority users action success`
);

export const navigateToCancellation = createAction(
  '[Authority users settings wizard page] Click on cancel link'
);

export const cancelAuthoritySetting = createAction(
  '[Authority users settings cancellation page] Click on cancel button'
);

export const clearAuthoritySettingState = createAction(
  '[Registry administration Init method] clear authority setting state'
);
