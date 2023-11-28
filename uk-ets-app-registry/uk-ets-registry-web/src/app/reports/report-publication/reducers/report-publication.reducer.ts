import { createReducer } from '@ngrx/store';
import { mutableOn } from '@registry-web/shared/mutable-on';
import { PublicationHistory, Section } from '@report-publication/model';
import {
  clearReportPublication,
  getReportPublicationSectionSuccess,
  loadReportPublicationHistorySuccess,
  loadReportPublicationSectionsSuccess,
  selectedPublicationFile,
  setSelectedId,
} from '@report-publication/actions';

export const reportPublicationFeatureKey = 'report-publication';

export interface ReportPublicationState {
  sections: Section[];
  selectedId: number;
  selectedSection: Section;
  publicationHistory: PublicationHistory[];
  selectedPublicationFile: PublicationHistory;
}

export const initialState: ReportPublicationState = {
  sections: [],
  selectedId: null,
  selectedSection: null,
  publicationHistory: [],
  selectedPublicationFile: {
    id: null,
    fileName: null,
    applicableForYear: null,
    publishedOn: null,
  },
};

export const reducer = createReducer(
  initialState,
  mutableOn(loadReportPublicationSectionsSuccess, (state, { sections }) => {
    state.sections = sections;
  }),
  mutableOn(setSelectedId, (state, { selectedId }) => {
    state.selectedId = selectedId;
  }),
  mutableOn(getReportPublicationSectionSuccess, (state, { section }) => {
    state.selectedSection = section;
  }),
  mutableOn(
    loadReportPublicationHistorySuccess,
    (state, { publicationHistory }) => {
      state.publicationHistory = publicationHistory;
    }
  ),
  mutableOn(selectedPublicationFile, (state, { id, fileName, fileYear }) => {
    state.selectedPublicationFile.id = id;
    state.selectedPublicationFile.fileName = fileName;
    state.selectedPublicationFile.applicableForYear = fileYear;
  }),
  mutableOn(clearReportPublication, (state) => {
    resetState(state);
  })
);

function resetState(state) {
  state.sections = initialState.sections;
  state.selectedSection = initialState.selectedSection;
  state.publicationHistory = initialState.publicationHistory;
  state.selectedPublicationFile = initialState.selectedPublicationFile;
}
