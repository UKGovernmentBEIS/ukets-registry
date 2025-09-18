import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  publicationDetailsUpdateReducerFeatureKey,
  UpdatePublicationDetailsState,
} from '@report-publication/components/update-publication-details/reducers/update-publication-details.reducer';
import { selectSection } from '@report-publication/selectors';
import { PublicationFrequency } from '@report-publication/model';
import { PublicationUtils } from '@shared/utils/publication.utils';

const selectUpdatePublicationDetailsState =
  createFeatureSelector<UpdatePublicationDetailsState>(
    publicationDetailsUpdateReducerFeatureKey
  );

export const selectUpdatedSectionDetails = createSelector(
  selectUpdatePublicationDetailsState,
  selectSection,
  (updatedState, state) => {
    if (updatedState.title && updatedState.summary) {
      return {
        ...state,
        title: updatedState.title,
        summary: updatedState.summary,
      };
    }
    return state;
  }
);

export const selectUpdatedPublicationDetails = createSelector(
  selectUpdatePublicationDetailsState,
  selectSection,
  (updatedState, state) => {
    switch (updatedState.reportPublicationFrequency) {
      case PublicationFrequency.DISABLED:
        return {
          ...state,
          publicationFrequency: updatedState.reportPublicationFrequency,
        };
      case PublicationFrequency.EVERY_X_DAYS:
        return {
          ...state,
          publicationFrequency: updatedState.reportPublicationFrequency,
          everyXDays: updatedState.everyXDays,
          publicationStart: updatedState.publicationStart,
          publicationTime: updatedState.publicationTime,
          generationDate: updatedState.generationDate,
          generationTime: updatedState.generationTime,
        };
      case PublicationFrequency.DAILY:
      case PublicationFrequency.YEARLY:
        return {
          ...state,
          publicationFrequency: updatedState.reportPublicationFrequency,
          publicationStart: updatedState.publicationStart,
          publicationTime: updatedState.publicationTime,
          generationDate: updatedState.generationDate,
          generationTime: updatedState.generationTime,
        };
      default:
        return state;
    }
  }
);

export const selectUpdatedDetailsForCheckAndSubmit = (needsConversion) =>
  createSelector(
    selectUpdatePublicationDetailsState,
    selectSection,
    (updatedState, currentState) => {
      if (needsConversion) {
        return PublicationUtils.convertPublicationTimeZone(
          {
            id: updatedState.id,
            title: updatedState.title,
            summary: updatedState.summary,
            displayType: updatedState.displayType,
            reportType: updatedState.reportType,
            publicationFrequency:
              PublicationFrequency[updatedState.reportPublicationFrequency],
            everyXDays: updatedState.everyXDays,
            publicationStart: updatedState.publicationStart,
            publicationTime: updatedState.publicationTime,
            generationDate: updatedState.generationDate,
            generationTime: updatedState.generationTime,
          },
          true
        );
      }
      return {
        id: currentState.id,
        title: updatedState.title,
        summary: updatedState.summary,
        displayType: updatedState.displayType,
        reportType: updatedState.reportType,
        publicationFrequency:
          PublicationFrequency[updatedState.reportPublicationFrequency],
        everyXDays: updatedState.everyXDays,
        publicationStart: updatedState.publicationStart,
        publicationTime: updatedState.publicationTime,
        generationDate: updatedState.generationDate,
        generationTime: updatedState.generationTime,
      };
    }
  );
