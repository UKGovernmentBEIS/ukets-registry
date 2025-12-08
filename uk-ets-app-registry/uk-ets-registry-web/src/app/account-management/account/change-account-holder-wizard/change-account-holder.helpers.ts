import {
  AccountHolder,
  AccountHolderAddress,
  AccountHolderContact,
  AccountHolderSelectionType,
  AccountHolderType,
  Individual,
  Organisation,
} from '@registry-web/shared/model/account';
import { isNil } from '@registry-web/shared/shared.util';
import { ChangeAccountHolderWizardState } from '@change-account-holder-wizard/store/reducers';

export const isExistingAccountHolder = (
  accountHolderSelectionType: AccountHolderSelectionType
): boolean => {
  return (
    accountHolderSelectionType === AccountHolderSelectionType.FROM_LIST ||
    accountHolderSelectionType === AccountHolderSelectionType.FROM_SEARCH
  );
};

export const isWizardCompleted = (
  state: ChangeAccountHolderWizardState
): boolean => {
  const accountHolderCompleted = isAccountHolderCompleted(
    state.acquiringAccountHolder,
    state.accountHolderSelectionType === AccountHolderSelectionType.NEW
  );
  const accountHolderContactCompleted = isAccountHolderContactCompleted(
    state.acquiringAccountHolderContact,
    state.accountHolderSelectionType === AccountHolderSelectionType.NEW
  );
  const accountHolderOrphanStepCompleted =
    state.isAccountHolderOrphan === false ||
    (state.isAccountHolderOrphan && !isNil(state.accountHolderDelete));

  return (
    accountHolderCompleted &&
    accountHolderContactCompleted &&
    accountHolderOrphanStepCompleted
  );
};

const isAccountHolderCompleted = (
  accountHolder: AccountHolder,
  isNewAccountHolder: boolean
): boolean => {
  if (!isNewAccountHolder) {
    return (
      !isNil(accountHolder.id) &&
      (accountHolder.type === AccountHolderType.ORGANISATION ||
        accountHolder.type === AccountHolderType.INDIVIDUAL)
    );
  }

  // Checks for new Account Holder
  let detailsCompleted = false;
  const addressCompleted = isAddressCompleted(accountHolder.address);

  if (accountHolder.type === AccountHolderType.ORGANISATION) {
    const details = (accountHolder as Organisation).details;
    detailsCompleted =
      !!details?.name &&
      (!!details?.registrationNumber ||
        !!details?.noRegistrationNumJustification);
    return detailsCompleted && addressCompleted;
  }

  if (accountHolder.type === AccountHolderType.INDIVIDUAL) {
    const individual = accountHolder as Individual;
    detailsCompleted =
      !!individual.details?.firstName &&
      !!individual.details?.lastName &&
      !!individual.details?.countryOfBirth;

    return (
      detailsCompleted &&
      addressCompleted &&
      !isNil(individual.phoneNumber) &&
      !isNil(individual.emailAddress)
    );
  }

  return false;
};

const isAccountHolderContactCompleted = (
  contact: AccountHolderContact,
  isNewAccountHolder: boolean
): boolean => {
  if (!isNewAccountHolder) {
    return true;
  }

  // Checks for new Account Holder
  const detailsCompleted =
    !!contact.details?.firstName && !!contact.details?.lastName;
  const addressCompleted = isAddressCompleted(contact.address);

  return (
    detailsCompleted &&
    addressCompleted &&
    !isNil(contact.phoneNumber) &&
    !isNil(contact.emailAddress) &&
    !!contact.positionInCompany
  );
};

const isAddressCompleted = (
  address: AccountHolderAddress | AccountHolderContact['address']
): boolean => {
  return (
    !!address?.buildingAndStreet &&
    !!address?.townOrCity &&
    !!address?.country &&
    (address?.country !== 'UK' || !!address?.postCode)
  );
};
