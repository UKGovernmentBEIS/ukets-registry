import { ApiErrorBody } from '@shared/api-error/api-error';
import {
  apiErrorToBusinessError,
  apiErrorToStringMessage
} from '@shared/model/transaction';

function aTestApiErrorBody(): ApiErrorBody {
  return {
    errorDetails: [
      {
        code: 'testCode1',
        message: 'testMessage1',
        urid: 'testUrid1',
        identifier: '10001'
      },
      {
        code: 'testCode2',
        message: 'testMessage2',
        urid: 'testUrid2',
        identifier: '10002'
      }
    ]
  };
}

describe('BusinessCheckError', () => {
  test('Api errors are translated into business errors', () => {
    expect(apiErrorToBusinessError(aTestApiErrorBody()).errors).toStrictEqual([
      {
        code: 'testCode1',
        message: 'testMessage1'
      },
      {
        code: 'testCode2',
        message: 'testMessage2'
      }
    ]);
  });

  test('Api errors are translated into messages', () => {
    expect(apiErrorToStringMessage(aTestApiErrorBody())).toBe('testMessage1');
  });
});
