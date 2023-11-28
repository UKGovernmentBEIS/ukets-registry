import { Pipe, PipeTransform } from '@angular/core';
import {
  COMMITMENT_PERIOD_LABELS,
  CommitmentPeriod,
  CommonTransactionSummary,
  UNIT_TYPE_LABELS,
} from '@shared/model/transaction';

/**
 * A pipe to render the unit type and SOP of the transaction Block summary
 */
@Pipe({
  name: 'unitTypeSopRender',
})
export class UnitTypeSopRenderPipe implements PipeTransform {
  sopAAUMap = new Map<boolean, string>([
    [true, ' (Subject to SOP)'],
    [false, ' (Not Subject to SOP)'],
    [null, ''],
  ]);

  transform(value: CommonTransactionSummary, args?: any): any {
    return value.type === 'AAU' &&
      value.originalPeriod === COMMITMENT_PERIOD_LABELS[CommitmentPeriod.CP2] &&
      value.applicablePeriod === COMMITMENT_PERIOD_LABELS[CommitmentPeriod.CP2]
      ? UNIT_TYPE_LABELS[value.type].labelPlural +
          this.sopAAUMap.get(value.subjectToSop)
      : this.isERUConvertedForSOP(value)
      ? UNIT_TYPE_LABELS[value.type].labelPlural + ' (Converted for SOP)'
      : UNIT_TYPE_LABELS[value.type].labelPlural;
  }

  isERUConvertedForSOP(value: CommonTransactionSummary) {
    return (
      (value.type === 'ERU_FROM_AAU' || value.type === 'ERU_FROM_RMU') &&
      value.subjectToSop &&
      value.originalPeriod === COMMITMENT_PERIOD_LABELS[CommitmentPeriod.CP2] &&
      value.applicablePeriod === COMMITMENT_PERIOD_LABELS[CommitmentPeriod.CP2]
    );
  }
}
