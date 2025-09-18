import { createAction, props } from '@ngrx/store';
import {
  RemoveRecoveryEmailRequest,
  RemoveRecoveryPhoneRequest,
  ResetRecoveryEmailStateRequest,
  ResetRecoveryPhoneStateRequest,
  UpdateRecoveryEmailRequest,
  UpdateRecoveryEmailResponse,
  UpdateRecoveryEmailVerificationRequest,
  UpdateRecoveryPhoneRequest,
  UpdateRecoveryPhoneResponse,
  UpdateRecoveryPhoneVerificationRequest,
} from '../recovery-methods-change.models';
import { HttpErrorResponse } from '@angular/common/http';

export const recoveryMethodsActions = {
  /*
   * UPDATE RECOVERY PHONE
   */
  NAVIGATE_TO_UPDATE_RECOVERY_PHONE_WIZARD: createAction(
    '[Recovery Methods] Navigate to Update Recovery Phone Wizard',
    props<{
      caller: { route: string };
      recoveryCountryCode: string;
      recoveryPhoneNumber: string;
      workMobileCountryCode: string;
      workMobilePhoneNumber: string;
    }>()
  ),
  REQUEST_UPDATE_RECOVERY_PHONE: createAction(
    '[Recovery Methods] Submit request to Update Recovery Phone',
    props<{ request: UpdateRecoveryPhoneRequest; caller: { route: string } }>()
  ),
  REQUEST_UPDATE_RECOVERY_PHONE_SUCCESS: createAction(
    '[Recovery Methods] Submit request to Update Recovery Phone Success',
    props<{ response: UpdateRecoveryPhoneResponse }>()
  ),
  REQUEST_UPDATE_RECOVERY_PHONE_ERROR: createAction(
    '[Recovery Methods] Submit request to Update Recovery Phone Error',
    props<{ error: HttpErrorResponse }>()
  ),
  NAVIGATE_TO_UPDATE_RECOVERY_PHONE_VERIFICATION: createAction(
    '[Recovery Methods] Navigate to Update Recovery Phone Verification'
  ),
  REQUEST_RESEND_UPDATE_RECOVERY_PHONE_SECURITY_CODE: createAction(
    '[Recovery Methods] Request Resend Update Recovery Phone Verification Code'
  ),
  REQUEST_RESEND_UPDATE_RECOVERY_PHONE_SECURITY_CODE_SUCCESS: createAction(
    '[Recovery Methods] Request Resend Update Recovery Phone Verification Code Success',
    props<{ response: UpdateRecoveryPhoneResponse }>()
  ),
  REQUEST_RESEND_UPDATE_RECOVERY_PHONE_SECURITY_CODE_ERROR: createAction(
    '[Recovery Methods] Request Resend Update Recovery Phone Verification Code Error',
    props<{ error: HttpErrorResponse }>()
  ),
  REQUEST_UPDATE_RECOVERY_PHONE_VERIFICATION: createAction(
    '[Recovery Methods] Submit request to Verify Updated Recovery Phone',
    props<{ request: UpdateRecoveryPhoneVerificationRequest }>()
  ),
  REQUEST_UPDATE_RECOVERY_PHONE_VERIFICATION_SUCCESS: createAction(
    '[Recovery Methods] Submit request to Verify Updated Recovery Phone Success'
  ),
  REQUEST_UPDATE_RECOVERY_PHONE_VERIFICATION_ERROR: createAction(
    '[Recovery Methods] Submit request to Verify Updated Recovery Phone Error',
    props<{ error: HttpErrorResponse }>()
  ),
  NAVIGATE_TO_UPDATE_RECOVERY_PHONE_CONFIRMATION: createAction(
    '[Recovery Methods] Navigate to Success Confirmation for Update Recovery Phone'
  ),
  RESET_RECOVERY_PHONE_FROM_STATE: createAction(
    '[Recovery Methods] Reset Recovery phone state',
    props<{ request: ResetRecoveryPhoneStateRequest }>()
  ),

  /*
   * REMOVE RECOVERY PHONE
   */
  NAVIGATE_TO_REMOVE_RECOVERY_PHONE_WIZARD: createAction(
    '[Recovery Methods] Navigate to Remove Recovery Phone Wizard',
    props<{ caller: { route: string } }>()
  ),
  REQUEST_REMOVE_RECOVERY_PHONE: createAction(
    '[Recovery Methods] Submit request to Remove Recovery Phone',
    props<{ request: RemoveRecoveryPhoneRequest }>()
  ),
  REQUEST_REMOVE_RECOVERY_PHONE_SUCCESS: createAction(
    '[Recovery Methods] Submit request to Remove Recovery Phone Success'
  ),
  REQUEST_REMOVE_RECOVERY_PHONE_ERROR: createAction(
    '[Recovery Methods] Submit request to Remove Recovery Phone Error',
    props<{ error: HttpErrorResponse }>()
  ),
  NAVIGATE_TO_REMOVE_RECOVERY_PHONE_CONFIRMATION: createAction(
    '[Recovery Methods] Navigate to Success Confirmation for Remove Recovery Phone'
  ),

  /*
   * UPDATE RECOVERY EMAIL
   */
  NAVIGATE_TO_UPDATE_RECOVERY_EMAIL_WIZARD: createAction(
    '[Recovery Methods] Navigate to Update Recovery Email Wizard',
    props<{
      caller: { route: string };
      recoveryEmailAddress: string;
    }>()
  ),
  REQUEST_UPDATE_RECOVERY_EMAIL: createAction(
    '[Recovery Methods] Submit request to Update Recovery Email',
    props<{ request: UpdateRecoveryEmailRequest; caller: { route: string } }>()
  ),
  REQUEST_UPDATE_RECOVERY_EMAIL_SUCCESS: createAction(
    '[Recovery Methods] Submit request to Update Recovery Email Success',
    props<{ response: UpdateRecoveryEmailResponse }>()
  ),
  REQUEST_UPDATE_RECOVERY_EMAIL_ERROR: createAction(
    '[Recovery Methods] Submit request to Update Recovery Email Error',
    props<{ error: HttpErrorResponse }>()
  ),
  NAVIGATE_TO_UPDATE_RECOVERY_EMAIL_VERIFICATION: createAction(
    '[Recovery Methods] Navigate to Update Recovery Email Verification'
  ),
  REQUEST_RESEND_UPDATE_RECOVERY_EMAIL_SECURITY_CODE: createAction(
    '[Recovery Methods] Request Resend Update Recovery Email Verification Code'
  ),
  REQUEST_RESEND_UPDATE_RECOVERY_EMAIL_SECURITY_CODE_SUCCESS: createAction(
    '[Recovery Methods] Request Resend Update Recovery Email Verification Code Success',
    props<{ response: UpdateRecoveryEmailResponse }>()
  ),
  REQUEST_RESEND_UPDATE_RECOVERY_EMAIL_SECURITY_CODE_ERROR: createAction(
    '[Recovery Methods] Request Resend Update Recovery Email Verification Code Error',
    props<{ error: HttpErrorResponse }>()
  ),
  REQUEST_UPDATE_RECOVERY_EMAIL_VERIFICATION: createAction(
    '[Recovery Methods] Submit request to Verify Updated Recovery Email',
    props<{ request: UpdateRecoveryEmailVerificationRequest }>()
  ),
  REQUEST_UPDATE_RECOVERY_EMAIL_VERIFICATION_SUCCESS: createAction(
    '[Recovery Methods] Submit request to Verify Updated Recovery Email Success'
  ),
  REQUEST_UPDATE_RECOVERY_EMAIL_VERIFICATION_ERROR: createAction(
    '[Recovery Methods] Submit request to Verify Updated Recovery Email Error',
    props<{ error: HttpErrorResponse }>()
  ),
  NAVIGATE_TO_UPDATE_RECOVERY_EMAIL_CONFIRMATION: createAction(
    '[Recovery Methods] Navigate to Success Confirmation for Update Recovery Email'
  ),
  RESET_RECOVERY_EMAIL_FROM_STATE: createAction(
    '[Recovery Methods] Reset Recovery phone state',
    props<{ request: ResetRecoveryEmailStateRequest }>()
  ),

  /*
   * REMOVE RECOVERY EMAIL
   */
  NAVIGATE_TO_REMOVE_RECOVERY_EMAIL_WIZARD: createAction(
    '[Recovery Methods] Navigate to Remove Recovery Email Wizard',
    props<{ caller: { route: string } }>()
  ),
  REQUEST_REMOVE_RECOVERY_EMAIL: createAction(
    '[Recovery Methods] Submit request to Remove Recovery Email',
    props<{ request: RemoveRecoveryEmailRequest }>()
  ),
  REQUEST_REMOVE_RECOVERY_EMAIL_SUCCESS: createAction(
    '[Recovery Methods] Submit request to Remove Recovery Email Success'
  ),
  REQUEST_REMOVE_RECOVERY_EMAIL_ERROR: createAction(
    '[Recovery Methods] Submit request to Remove Recovery Email Error',
    props<{ error: HttpErrorResponse }>()
  ),
  NAVIGATE_TO_REMOVE_RECOVERY_EMAIL_CONFIRMATION: createAction(
    '[Recovery Methods] Navigate to Success Confirmation for Remove Recovery Email'
  ),
  REQUEST_PHONE_SET_EXPIRED_AT: createAction(
    '[Recovery Methods] Recovery Phone Expired at',
    props<{ expiredAt: number }>()
  ),

  REQUEST_EMAIL_SET_EXPIRED_AT: createAction(
    '[Recovery Methods] Recovery Email Set Expired at',
    props<{ expiredAt: number }>()
  ),
};
