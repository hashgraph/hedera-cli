import {
  CustomFixedFee,
  CustomFractionalFee,
  Hbar,
  AccountId,
  CustomFee,
} from '@hashgraph/sdk';
import { DomainError } from './errors';
import { FixedFee, FractionalFee } from '../../types';
function createCustomFixedFee(fee: FixedFee): CustomFee {
  const customFee = new CustomFixedFee();

  switch (fee.unitType.toLowerCase()) {
    case 'hbar':
    case 'hbars':
      customFee.setHbarAmount(new Hbar(fee.amount));
      break;
    case 'tinybar':
    case 'tinybars':
      customFee.setHbarAmount(Hbar.fromTinybars(fee.amount));
      break;
    case 'token':
    case 'tokens':
      if (!fee.denom) {
        throw new DomainError('Token fee requires denom property');
      }
      customFee.setAmount(fee.amount);
      customFee.setDenominatingTokenId(fee.denom);
      break;
    default:
      throw new DomainError(`Invalid fee unit type: ${fee.unitType}`);
  }

  if (fee.collectorId) {
    customFee.setFeeCollectorAccountId(AccountId.fromString(fee.collectorId));
  }
  if (fee.exempt) {
    customFee.setAllCollectorsAreExempt(fee.exempt);
  }

  return customFee;
}

function createCustomFractionalFee(fee: FractionalFee): CustomFee {
  const customFee = new CustomFractionalFee()
    .setNumerator(fee.numerator)
    .setDenominator(fee.denominator);

  if (fee.min) customFee.setMin(fee.min);
  if (fee.max) customFee.setMax(fee.max);

  if (fee.collectorId) {
    customFee.setFeeCollectorAccountId(AccountId.fromString(fee.collectorId));
  }
  if (fee.exempt) {
    customFee.setAllCollectorsAreExempt(fee.exempt);
  }

  return customFee;
}

const tokenUtils = {
  createCustomFixedFee,
  createCustomFractionalFee,
};

export default tokenUtils;
