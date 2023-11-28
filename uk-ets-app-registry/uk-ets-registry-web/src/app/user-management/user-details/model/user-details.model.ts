export enum ViewMode {
  USER_DETAILS = 'USER_DETAILS',
  MY_PROFILE = 'MY_PROFILE'
}

export const VIEW_MODE_LABELS: Record<
  ViewMode,
  { label: string; description?: string }
> = {
  USER_DETAILS: { label: 'User details' },
  MY_PROFILE: { label: 'My profile' }
};
