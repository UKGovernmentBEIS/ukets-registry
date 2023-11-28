import { Given, Then, When } from 'cucumber';
import { createUser, GlobalState } from './ui-step-definitions';
import { HttpClient } from 'protractor-http-client/dist/http-client';
import { environment } from '../util/environment-configuration';
import { expect } from 'chai';
import { ResponsePromise } from 'protractor-http-client/dist/promisewrappers';
import { browser } from 'protractor';

let accessToken;
let apiResponse;
const TOKEN_ENDPOINT = '/realms/uk-ets/protocol/openid-connect/token';
const GRANT_TYPE_UMA_TICKET = encodeURI(
  'urn:ietf:params:oauth:grant-type:uma-ticket'
);

Given(
  'the client has been authenticated as {string} user',
  async (userRole: string) => {
    console.log(
      `Executing step: The client has been authenticated as "${userRole}" user.`
    );
    browser.waitForAngularEnabled(false);
    let email: string;
    let thePassword: string;
    if (userRole === 'registry admin') {
      email = thePassword = 'uk-ets-admin';
    } else {
      GlobalState.currentUser = await createUser(userRole, 0);
      email = GlobalState.currentUser.email;
      thePassword = GlobalState.currentUser.password;
    }
    const httpClient = new HttpClient(environment().keycloakBaseUrl);
    const tokenResponse: ResponsePromise = httpClient.post(
      TOKEN_ENDPOINT,
      `grant_type=password&client_id=uk-ets-web-app&username=${email}&password=${thePassword}`,
      { 'Content-Type': 'application/x-www-form-urlencoded' }
    );
    expect(await tokenResponse.statusCode).to.equal(200);
    accessToken = await tokenResponse.jsonBody.get('access_token');
    console.log('Initial access token was received.');
  }
);

When(
  'the client requests {string} {string}',
  async (httpMethod: 'POST' | 'GET' | 'PUT', action: string) => {
    const httpClient = new HttpClient();
    apiResponse = httpClient.request({
      method: httpMethod,
      url: environment().registryApiUrl + action,
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });
    const ticket = await extractPermissionTicket();
    const tokenResponse: ResponsePromise = httpClient.post(
      environment().keycloakBaseUrl + TOKEN_ENDPOINT,
      `grant_type=${GRANT_TYPE_UMA_TICKET}&audience=uk-ets-registry-api&ticket=${ticket}`,
      {
        'Content-Type': 'application/x-www-form-urlencoded',
        Authorization: `Bearer  ${accessToken}`,
      }
    );

    const registryAPIBearerToken = await tokenResponse.jsonBody.get(
      'access_token'
    );
    apiResponse = httpClient.request({
      method: httpMethod,
      url: environment().registryApiUrl + action,
      headers: {
        Authorization: `Bearer ${registryAPIBearerToken}`,
      },
    });
  }
);

Then('the response status code should be {int}', async (status: number) => {
  expect(await apiResponse.statusCode).to.equal(status);
});

Then(
  'the response contains element {string} with value:',
  async (givenElement: string, value: string) => {
    expect((await apiResponse.jsonBody.get(givenElement)).toString()).to.equal(
      value
    );
  }
);

async function extractPermissionTicket() {
  const headers: string | string[] = await apiResponse.header(
    'WWW-Authenticate'
  );
  const header: string = Array.isArray(headers) ? headers[0] : headers;
  const ticket = header.split(',')[2].split('=')[1].replace(/["]+/g, '');
  return ticket;
}
