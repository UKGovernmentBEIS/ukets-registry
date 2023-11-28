import { createAction, props } from '@ngrx/store';
import { Section } from '@report-publication/model';

export const submitSectionDetails = createAction(
  '[Publication Details Wizard] Submit updated section details',
  props<{ sectionDetails: Section }>()
);

export const submitPublicationDetails = createAction(
  '[Publication Details Wizard] Submit updated publication details',
  props<{ publicationDetails: Section }>()
);

export const submitUpdateRequest = createAction(
  '[Publication Details Wizard] Submit publication details update request'
);

export const submitUpdateRequestSuccess = createAction(
  '[Publication Details Wizard] Submit publication details update request success'
);

export const clearPublicationDetailsWizard =  createAction(
  '[Publication Details Wizard] Clear publication details update  wizard');
