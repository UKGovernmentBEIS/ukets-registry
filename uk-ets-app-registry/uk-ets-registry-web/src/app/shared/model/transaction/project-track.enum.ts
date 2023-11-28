export enum ProjectTrack {
  TRACK_1 = 'TRACK_1',
  TRACK_2 = 'TRACK_2'
}

export const PROJECT_TRACK_LABELS: Record<
  ProjectTrack,
  { label: string; description?: string }
> = {
  TRACK_1: { label: 'Track 1' },
  TRACK_2: { label: 'Track 2' }
};
