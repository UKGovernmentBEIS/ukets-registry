import { DisplayType, PublicationFrequency } from '@report-publication/model';
import { ReportType } from '@reports/model';
import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  clearPublicationDetailsWizard,
  submitPublicationDetails,
  submitSectionDetails,
} from '@report-publication/components/update-publication-details/actions/update-publication-details.actions';

export const publicationDetailsUpdateReducerFeatureKey =
  'publication-details-update';

export interface UpdatePublicationDetailsState {
  id: number;
  reportType: ReportType;
  title: string;
  summary: string;
  displayType: DisplayType;
  reportPublicationFrequency: PublicationFrequency;
  everyXDays?: number;
  publicationStart: string;
  publicationTime: string;
  generationDate: string;
  generationTime: string;
}

const initialState: UpdatePublicationDetailsState = getInitialState();

const updatePublicationDetailsReducer = createReducer(
  initialState,
  mutableOn(submitSectionDetails, (state, { sectionDetails }) => {
    state.title = sectionDetails.title;
    state.summary = sectionDetails.summary;
  }),
  mutableOn(submitPublicationDetails, (state, { publicationDetails }) => {
    state.reportPublicationFrequency = publicationDetails.publicationFrequency;
    state.everyXDays = publicationDetails.everyXDays;
    state.publicationStart = publicationDetails.publicationStart;
    state.publicationTime = publicationDetails.publicationTime;
    state.generationDate = publicationDetails.generationDate;
    state.generationTime = publicationDetails.generationTime;
  }),
  mutableOn(clearPublicationDetailsWizard, (state) => {
    resetState(state);
  })
);

function resetState(state) {
  state.id = getInitialState().id;
  state.displayType = getInitialState().displayType;
  state.generationDate = getInitialState().generationDate;
  state.generationTime = getInitialState().generationTime;
  state.publicationStart = getInitialState().publicationStart;
  state.publicationTime = getInitialState().publicationTime;
  state.everyXDays = getInitialState().everyXDays;
  state.reportPublicationFrequency =
    getInitialState().reportPublicationFrequency;
  state.reportType = getInitialState().reportType;
  state.summary = getInitialState().summary;
  state.title = getInitialState().title;
}

export function reducer(state: UpdatePublicationDetailsState, action: Action) {
  return updatePublicationDetailsReducer(state, action);
}

function getInitialState(): UpdatePublicationDetailsState {
  return {
    id: null,
    displayType: null,
    generationDate: null,
    generationTime: null,
    publicationStart: null,
    publicationTime: null,
    reportPublicationFrequency: null,
    everyXDays: null,
    reportType: null,
    summary: null,
    title: null,
  };
}
