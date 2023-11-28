/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

import { RegistryScreen } from '../../util/screens';

export class KnowsTheProgressBarComponent {
  private static progressBarItemPerScreen: Map<string, string> = new Map<
    string,
    string
  >()
    .set(RegistryScreen.PERSONAL_DETAILS, 'Enter personal details')
    .set(RegistryScreen.WORK_CONTACT_DETAILS, 'Enter work contact details')
    .set(RegistryScreen.CHOOSE_PWD, 'Choose password')
    .set(
      RegistryScreen.CHECK_ANSWERS_AND_SUBMIT,
      'Check your answers and submit'
    )
    .set(RegistryScreen.REGISTERED, 'Registered')
    .set(RegistryScreen.EMAIL_VERIFY, 'Verify your email');

  public static getTextByScreenName(screenName: string) {
    return KnowsTheProgressBarComponent.progressBarItemPerScreen.get(
      screenName
    );
  }
}
