import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromReportPublication from '@report-publication/reducers';
import { PublicationUtils } from '@shared/utils/publication.utils';

export const selectReportPublicationState =
  createFeatureSelector<fromReportPublication.ReportPublicationState>(
    fromReportPublication.reportPublicationFeatureKey
  );

export const selectReportPublicationSections = createSelector(
  selectReportPublicationState,
  (state) => state.sections
);

export const selectReportPublicationSectionId = createSelector(
  selectReportPublicationState,
  (state) => state.selectedId
);

export const selectSection = createSelector(
  selectReportPublicationState,
  (state) => {
    return PublicationUtils.convertPublicationTimeZone(
      state.selectedSection,
      false
    );
  }
);

export const selectReportPublicationHistory = createSelector(
  selectReportPublicationState,
  (state) => state.publicationHistory
);

export const selectPublicationFile = createSelector(
  selectReportPublicationState,
  (state) => state.selectedPublicationFile
);
