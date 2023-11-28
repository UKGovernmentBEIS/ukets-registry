import { ApiErrorBody } from '@shared/api-error/api-error';
import { apiErrorToBusinessError } from '.';

describe('accountModel', () => {
  test('api errors are converted to business errors', () => {
    expect(apiErrorToBusinessError(aTestApiErrorBody()).error).toStrictEqual({
      code: 'testCode1',
      message: 'testMessage1',
      accountFullIdentifier: 10001,
      urid: 'testUrid1',
    });
  });
});

function aTestApiErrorBody(): ApiErrorBody {
  return {
    errorDetails: [
      {
        code: 'testCode1',
        message: 'testMessage1',
        urid: 'testUrid1',
        identifier: '10001',
      },
    ],
  };
}
