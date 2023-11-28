export enum UserUpdateDetailsType {
  UPDATE_USER_DETAILS = 'UPDATE_USER_DETAILS',
  DEACTIVATE_USER = 'DEACTIVATE_USER',
}

export const UpdateUserDetailsRequestTypeMap: Record<
  UserUpdateDetailsType,
  {
    label: string;
    enabled: boolean;
    userLabel: string;
    userHint: string;
    adminLabel: string;
    adminHint: string;
    cancelRequestTitle: string;
    cancelRequestText: string;
  }
> = {
  UPDATE_USER_DETAILS: {
    label: 'Update user details',
    enabled: true,
    userLabel: 'Update your user details',
    userHint: 'Update your personal or work details',
    adminLabel: 'Update the user details',
    adminHint:
      "Update this user's memorable phrase, personal details or work details",
    cancelRequestTitle: 'Cancel user details update',
    cancelRequestText: 'user details update',
  },
  DEACTIVATE_USER: {
    label: 'Deactivate user',
    enabled: true,
    userLabel: 'Deactivate your Registry access',
    userHint: 'Request to permanently disable your sign in to the Registry',
    adminLabel: 'Deactivate user',
    adminHint: "Permanently disable this user's sign in",
    cancelRequestTitle: 'Cancel user deactivation',
    cancelRequestText: 'user deactivation',
  },
};
