import { KeycloakClient } from '../../../../keycloak-client.util';
import { RegistryDbUserBuilder } from '../model/registry-db-user.builder';
import { RegistryDbUser } from '../model/registry-db-user';
import { RegistryDbTestUser } from '../model/registry-db-test-user';
import { KeycloakUser } from '../model/keycloak-user';
import { KeycloakUserBuilder } from '../model/keycloak-user.builder';
import { RegistryDbUrids } from './registry-db-urids';
import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import {
  convertBase32DataToAscii,
  delay,
  getCurrentMomentByFormat,
  getRandomString,
  isSubstringInsensitivelyIncludedIntoArrayItems,
} from '../../../../step.util';
import { getMapEntryValueAttributeByKey } from '../../../../user-2fa-map-utils';
import { KnowsThePage } from '../../../../knows-the-page.po';
import { TwoFactorAuthenticationUtils } from '../../../../two-factor-authentication-utils';
import { PostgrestClient } from '../../../postgrest-client.util';
import { environment } from '../../../../environment-configuration';

const keycloakClient = new KeycloakClient('uk-ets');
const clientNameRegistry = 'uk-ets-registry-api';
const clientNameReports = 'uk-ets-reports-api';
const reportsRoleName = `reports-user`;

export class RegistryDbUserTestData {
  static sequenceId = 100000001;
  static sequenceUrIdPosition = 0;

  static nextId() {
    return this.sequenceId++;
  }

  static resetIndices() {
    try {
      this.sequenceId = 100000001;
      this.sequenceUrIdPosition = 0;
    } catch (e) {
      console.log(`Could not resetIndices.`);
    }
  }

  static async createUser(
    testUser: RegistryDbTestUser,
    daysPassedBeforeActivationCodeIssued: number
  ) {
    const currentUrid = RegistryDbUrids.ids[this.sequenceUrIdPosition++];
    console.log(`currentUrid: '${currentUrid}'.`);
      
    const enrollmentKeyValue =
      getRandomString(4, 'all alphabet uppercase characters and all numbers') +
      '-' +
      getRandomString(4, 'all alphabet uppercase characters and all numbers') +
      '-' +
      getRandomString(4, 'all alphabet uppercase characters and all numbers') +
      '-' +
      getRandomString(4, 'all alphabet uppercase characters and all numbers') +
      '-' +
      getRandomString(4, 'all alphabet uppercase characters and all numbers');
    const enrollmentKeyDateValue = getCurrentMomentByFormat(
      daysPassedBeforeActivationCodeIssued,
      'YYYY-MM-DD hh:mm:ss'
    );

    // generate secret key in order to use it into the create user
    new TwoFactorAuthenticationUtils().setUserSignIGeneratedSecret(
      testUser.username,
      20
    );

    const keycloakUser: KeycloakUser = new KeycloakUserBuilder()
      .urid(currentUrid)
      .state(testUser.state)
      .firstName(testUser.firstName)
      .lastName(testUser.lastName)
      .username(testUser.username)
      .email(testUser.email)
      .otp(
        `{"value":"` +
          convertBase32DataToAscii(
            getMapEntryValueAttributeByKey(
              KnowsThePage.users2fa,
              testUser.username,
              'secret'
            )
          ) +
          `"}`
      )
      .build();

    await RegistryDbUserTestData.loadUsersInKeycloak([keycloakUser]);

    // console.log(`About create new RegistryDbUserBuilder`);
    const dbUser: RegistryDbUser = new RegistryDbUserBuilder()
      .urid(currentUrid)
      .first_name(testUser.firstName)
      .last_name(testUser.lastName)
      .state(testUser.state)
      .enrolment_key(
        testUser.state === 'UNENROLLED' ||
          testUser.state === 'UNENROLLED PENDING'
          ? ''
          : enrollmentKeyValue
      )
      .enrolment_key_date(
        testUser.state === 'UNENROLLED' ||
          testUser.state === 'UNENROLLED PENDING'
          ? ''
          : enrollmentKeyDateValue
      )
      .disclosed_name(testUser.disclosedName)
      .build();
    testUser.id = dbUser.id = String(this.nextId());

    console.log(`About to access RegistryDbUserTestData loadUsersInDB`);
    await delay(500);
    await RegistryDbUserTestData.loadUsersInDB([dbUser]);

    // load user roles AFTER registry db user insertions
    await this.addKeycloakRoles(currentUrid, testUser);

    return testUser;
  }

  static async loadUsersInKeycloak(users: KeycloakUser[]) {
    console.log(`Entered loadUsersInKeycloak with: '${users}'.`);
    await keycloakClient.auth();
    console.log(`About to call keycloakClient.createUsers(users)`);
    await keycloakClient.createUsers(users);
    console.log(`Exiting loadUsersInKeycloak`);
  }

  static async loadUsersInDB(users: RegistryDbUser[]) {
    await keycloakClient.auth();
    for (const user of users) {
      const userWithUrid = await keycloakClient.findUserByUrid(user.urid);
      if (userWithUrid) {
        user.iam_identifier = userWithUrid.id;
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.USERS_PATH,
          PathsAndTypesRegistryDb.USERS_TYPE,
          user
        );
      }
    }
  }

  static async deleteUsersFromKeycloakAndDB(urids: string[]) {
    for (const urid of urids) {
      await RegistryDbUserTestData.deleteUserFromKeycloakAndDB(urid);
    }
  }

  static async addKeycloakRoles(
    currentUrid: string,
    testUser: RegistryDbTestUser
  ) {
    // add REGISTRY roles
    for (const role of testUser.registryRoles) {
      await keycloakClient.addRoleToUser(currentUrid, clientNameRegistry, role);
    }

    // add REPORTS role in case of admin user
    if (
      isSubstringInsensitivelyIncludedIntoArrayItems(
        `admin`,
        testUser.registryRoles
      )
    ) {
      await keycloakClient.addRoleToUser(
        currentUrid,
        clientNameReports,
        reportsRoleName
      );
    }
  }

  static async deleteUserFromKeycloakAndDB(urid: string) {
   try {
    await keycloakClient.auth();
    const userWithUrid = await keycloakClient.findUserByUrid(urid);
    if (userWithUrid) {
      await keycloakClient.deleteUserById(userWithUrid.id);
      await PostgrestClient.genericRemove(
        environment().postgrestRegistryBaseUrl,
        PathsAndTypesRegistryDb.USER_ROLE_MAPPING_PATH,
        PathsAndTypesRegistryDb.USER_ROLE_MAPPING_TYPE
      );
      await PostgrestClient.genericRemove(
        environment().postgrestRegistryBaseUrl,
        PathsAndTypesRegistryDb.USERS_PATH,
        PathsAndTypesRegistryDb.USERS_TYPE
        // ,
        // urid,
        // false,
        // 'urid'
      );
    }
      } catch (ex) {
      console.error(`deleteUserFromKeycloakAndDB: user not deleted: '${ex}'.`);
    }
  }

  static async deleteAllUsersFromKeycloakAndDB() {
    await this.deleteUsersFromKeycloakAndDB(RegistryDbUrids.ids);
  }
}
