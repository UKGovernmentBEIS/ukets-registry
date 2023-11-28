export enum OUTCOME {
  SUCCESS = 'Successful',
  FAILURE = 'Unsuccessful'
}

export enum ACTION {
  INSERT = 'insertion',
  DELETE = 'deletion'
}

export class Messages {
  static getUserActionMessage(
    urid: string,
    id: string,
    iam_identifier: string,
    action: ACTION,
    outcome: OUTCOME,
    error: string
  ) {
    return `INFO:\t'${outcome}' '${action}' of user with URID: '${urid}', ID: '${id}' and IAM Identifier: '${iam_identifier}'. ${
      error ? 'Error: ' + error : ''
    }`;
  }

  static getGenericActionMessage(
    id: string,
    type: string,
    action: ACTION,
    outcome: OUTCOME,
    error: string
  ) {
    return `INFO:\t${outcome} ${action} of ${type} with ID: ${id}. ${
      error ? 'Error: ' + error : ''
    }`;
  }
}
