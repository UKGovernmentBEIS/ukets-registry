import {
  SummaryListInfo,
  SummaryListItem,
} from '@shared/summary-list/summary-list.info';

export class SummaryListAddressUtil {
  static calculateAddressLine2(current, changed) {
    if (current === '' && changed === undefined) {
      // for address line 2, when it is not present we want to show an empty row in the UI:
      return [
        {
          label: '',
          class: '',
        },
      ];
    }
    return this.getValue(current, changed, true);
  }

  static calculateAddressLine3(current, changed) {
    if (current === '' && changed === undefined) {
      return null;
    }
    return this.getValue(current, changed, true);
  }

  static constructAddressItems(
    addressLine1: SummaryListInfo[],
    addressLine2: SummaryListInfo[],
    addressLine3: SummaryListInfo[]
  ): SummaryListItem[] {
    let items = [
      {
        key: this.getKey('Address', false),
        value: addressLine1,
      },
    ];
    // in case that line 3 is present but not line 2, we DO want to show an (empty) line 2
    if (addressLine2 || addressLine3) {
      items = [
        ...items,
        {
          key: this.getKey('', false),
          value: addressLine2,
        },
      ];
    }
    if (addressLine3) {
      items = [
        ...items,
        {
          key: this.getKey(''),
          value: addressLine3,
        },
      ];
    }
    return items;
  }

  static getKey(name, hasBorder = true) {
    return {
      label: name,
      class: hasBorder
        ? 'govuk-summary-list__key'
        : 'govuk-summary-list--no-border',
    };
  }

  static getValue(current, changed, showChanged) {
    const baseClass = 'govuk-summary-list__value ';
    const items = [
      {
        label: current,
        class: baseClass,
      },
    ];
    if (showChanged) {
      items.push({
        label: changed,
        class: changed != undefined ? baseClass + 'focused-text' : baseClass,
      });
    }
    return items;
  }
}
