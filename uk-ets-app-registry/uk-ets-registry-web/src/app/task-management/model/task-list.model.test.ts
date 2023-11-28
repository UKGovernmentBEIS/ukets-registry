import { apiErrorToBusinessError } from '@task-management/model/task-list.model';
import { ApiErrorBody } from '@shared/api-error/api-error';

describe('TaskListModel', () => {
  test('api errors are converted to business errors', () => {
    expect(apiErrorToBusinessError(aTestApiErrorBody()).errors).toStrictEqual([
      {
        code: 'testCode1',
        message: 'testMessage1',
        requestId: 10001,
        urid: 'testUrid1'
      },
      {
        code: 'testCode2',
        message: 'testMessage2',
        requestId: 10002,
        urid: 'testUrid2'
      }
    ]);
  });
});

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
