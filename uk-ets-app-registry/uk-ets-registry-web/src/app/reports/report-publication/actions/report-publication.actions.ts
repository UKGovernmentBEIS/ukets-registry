import { createAction, props } from '@ngrx/store';
import { NavigationExtras } from '@angular/router';
import {
  PublicationHistory,
  Section,
  SectionType,
  SortActionPayload,
} from '@report-publication/model';

export const prepareNavigationLinksForWizards = createAction(
  '[Report Publication Wizards] - Prepare navigation links for wizards',
  props<{ routeSnapshotUrl: string }>()
);

export const canGoBackInWizards = createAction(
  '[Report Publication Wizards] - Can go back in wizards',
  props<{ specifyBackLink?: string; extras?: NavigationExtras }>()
);

export const navigateTo = createAction(
  '[Report Publication Wizards] - Navigate to',
  props<{ specifyLink?: string; extras?: NavigationExtras }>()
);

export const loadReportPublicationSections = createAction(
  '[Report Publication] Load Report Publication Sections',
  props<{ sectionType: SectionType }>()
);

export const loadReportPublicationSectionsSuccess = createAction(
  '[Report Publication] Load Report Publication Sections Success',
  props<{ sections: Section[] }>()
);

export const setSelectedId = createAction(
  '[Report Publication] Set Selected Section Id',
  props<{ selectedId: number }>()
);

export const selectedPublicationFile = createAction(
  '[Report Publication] Select Publication File',
  props<{ id: number; fileName: string; fileYear: number }>()
);

export const unpublishFile = createAction('Report Publication] Unpublish file');

export const unpublishFileSuccess = createAction(
  'Report Publication] Unpublish file success'
);

export const getReportPublicationSection = createAction(
  '[Report Publication] Get Report Publication Section'
);

export const getReportPublicationSectionSuccess = createAction(
  '[Report Publication] Get Report Publication Section Success',
  props<{ section: Section }>()
);

export const loadReportPublicationHistory = createAction(
  '[Report Publication] Load Report Publication History',
  props<SortActionPayload>()
);

export const loadReportPublicationHistorySuccess = createAction(
  '[Report Publication] Load Report Publication History Success',
  props<{ publicationHistory: PublicationHistory[] }>()
);

export const clearReportPublication = createAction(
  '[Report Publication] Clear Report Publication State'
);

export const downloadFile = createAction(
  'Report Publication] Download file',
  props<{ id: number }>()
);
