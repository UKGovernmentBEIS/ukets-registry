import { delay } from './step.util';
import { environment } from './environment-configuration';
import { assert } from 'chai';

const mailhog = require('mailhog')({
  host: environment().mailhogHost,
  port: environment().mailhogPort,
  protocol: environment().mailhogProtocol,
});

export class MailhogClient {
  timeoutInMillis: number;

  constructor(timeout: number = 60000) {
    this.timeoutInMillis = timeout;
  }

  async deleteAll() {
    await mailhog.deleteAll();
  }

  async getLatestEmail(
    expectedEmailSubject: string,
    expectedEmailAddress: string
  ): Promise<string> {
    let time = 0;
    const step = 1000;
    let actualLatestEmailData: any = null;

    console.log(
      'Entered getLatestEmail with expected: ' +
        expectedEmailSubject +
        ' expected email address: ' +
        expectedEmailAddress
    );

    while (time < this.timeoutInMillis * 2 && !actualLatestEmailData) {
      console.log(
        `actualLatestEmailData before mailhog.latestTo: '${actualLatestEmailData}'.`
      );
      await delay(step);
      time += step;
      actualLatestEmailData = await mailhog.latestTo(expectedEmailAddress);
      console.log(
        `actualLatestEmailData after mailhog.latestTo: '${actualLatestEmailData}'.`
      );
    }

    const latestEmailDataContentHeadersTo =
      actualLatestEmailData['Content']['Headers']['To'];
    console.log(
      `\nlatestEmailDataContentHeadersTo: '${JSON.stringify(
        latestEmailDataContentHeadersTo
      )}'.`
    );

    const latestEmailDataContentHeadersSubject =
      actualLatestEmailData['Content']['Headers']['Subject'];
    console.log(
      `\nlatestEmailDataContentHeadersSubject: '${JSON.stringify(
        latestEmailDataContentHeadersSubject
      )}'.`
    );

    const latestEmailDataContentBody = actualLatestEmailData['Content']['Body'];
    console.log(
      `\nlatestEmailDataContentBody: '${JSON.stringify(
        latestEmailDataContentBody
      )}'.`
    );

    // assertion on email title
    assert.equal(
      latestEmailDataContentHeadersSubject[0],
      expectedEmailSubject,
      `\nExpected subject is: '${expectedEmailSubject}'.\n` +
        `Actual subject is:    '${latestEmailDataContentHeadersSubject[0]}'\n.`
    );

    // assertion on email address
    assert.equal(
      latestEmailDataContentHeadersTo[0],
      expectedEmailAddress,
      `\nExpected address is: '${expectedEmailSubject}'.\n` +
        `Actual address is: '${latestEmailDataContentHeadersSubject[0]}'\n.`
    );

    return latestEmailDataContentBody;
  }
}
