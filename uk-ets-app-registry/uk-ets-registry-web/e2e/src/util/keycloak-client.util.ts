import KcAdminClient from '@keycloak/keycloak-admin-client';
import { environment } from './environment-configuration';
import { KnowsThePage } from './knows-the-page.po';
import { TwoFactorAuthenticationUtils } from './two-factor-authentication-utils';
import { getMapEntryValueAttributeByKey } from './user-2fa-map-utils';

export class KeycloakClient {
  kcAdminClient: KcAdminClient;

  constructor(private realmName: string) {
    this.kcAdminClient = new KcAdminClient({
      baseUrl: environment().keycloakBaseUrl,
      realmName: this.realmName,
      requestConfig: {
        proxy: false,
      },
    });
  }

  async auth() {
    console.log(`Entered Keycloak auth.`);
    await this.kcAdminClient.auth({
      username: environment().keycloakUsername,
      password: environment().keycloakPassword,
      grantType: 'password',
      clientId: 'admin-cli',
    });
    console.log(`Exiting Keycloak auth.`);
  }

  async createUsers(userToCreate: any) {
    const usn = userToCreate[0]['username'];
    console.log(`usn: '${usn}'.`);
    console.log(`userToCreate[0].username: '${userToCreate[0].username}'`);
    console.log(`userToCreate[0].attributes.urid: '${userToCreate[0].attributes.urid}'`);

    try {
    await Promise.all(
      userToCreate.map(async (user) =>
        this.kcAdminClient.users.create({
          ...user,
          realm: this.realmName,
        })
      )
    );
    } catch (ex) {
      console.error(`createUsers - userToCreate: '${ex}'.`);
    }

    await new TwoFactorAuthenticationUtils().setVerificationCodeManually(
      usn,
      getMapEntryValueAttributeByKey(KnowsThePage.users2fa, usn, 'secret'),
      'sha256',
      'totp',
      '6'
    );

    const correctCode = getMapEntryValueAttributeByKey(
      KnowsThePage.users2fa,
      usn,
      'otp'
    );
    console.log(`Username '${usn}' has now otp with value '${correctCode}'.`);
  }

  async deleteUserByEmail(usersEmail: string) {
    console.log(`Deleting user by email: '${usersEmail}'\n.`);
    try {
      const users = await this.findUsers();
      const userToDelete = users.find((user) => user.email === usersEmail);
      if (userToDelete) {
        const userId = userToDelete.id;
        await this.deleteUserById(userId);
      } else {
        console.log(
          `userId not found, user with email '${usersEmail}' will not be deleted.`
        );
      }
    } catch (e) {
      console.log(`Could not delete user by email: '${usersEmail}'.`);
      throw e;
    }
  }

  async deleteUsers(users: any[]) {
    console.log(`Deleting users with email: '${users}'\n.`);
    try {
      await Promise.all(
        users.map(async (user) => {
          const userFound = await this.findUser(user.email);
          if (userFound) {
            this.deleteUserById(userFound.id);
          } else {
            console.log(
              `userId not found, '${users}' user will not be deleted.`
            );
          }
        })
      );
    } catch (e) {
      console.log(`Could not delete user(s): '${users}'.`);
      throw e;
    }
  }

  async findUsers() {
    // console.log(`\nEntered findUsers`);
    return await this.kcAdminClient.users.find();
    // console.log(`Exiting findUsers\n`);
  }

  async findUser(email: string) {
    const users = await this.findUsers();
    return users.find((user) => user.email === email);
  }

  async findUserByUrid(urid: string) {
    try {
    console.log(`findUserByUrid: '${urid}'.`);
    const users = await this.findUsers();
    // console.log(`all users fetched, about to filter with specific urid.`);
    return users.find((user) => {
      if (user.attributes.urid === null) {
        return false;
      }
      else {
          // console.log(`user.attributes.urid: '${user.attributes.urid}'.`);
          const urId = user.attributes.urid;
          // console.log(`urId: '${urId}'.`);
          const returnValue = urId.indexOf(urid) > -1
          // console.log(`returnValue: '${returnValue}'.`);
          return returnValue;
        }
    });
    } catch (ex) {
      console.error(`findUserByUrid: exception '${ex}'.`);
    }
  }

  async deleteUserById(id: string) {
    await this.kcAdminClient.users.del({
      id,
    });
  }

  async addRoleToUser(urid: string, clientName: string, roleName: string) {
    console.log(
      `AddRoleToUser: urid: '${urid}', clientName: '${clientName}', roleName: '${roleName}'.`
    );
    const userWithUrid = await this.findUserByUrid(urid);
    const client = await this.kcAdminClient.clients.find({
      clientId: clientName,
    });
    const role = await this.kcAdminClient.clients.findRole({
      id: client[0].id,
      roleName,
    });
    await this.kcAdminClient.users.addClientRoleMappings({
      id: userWithUrid.id,
      clientUniqueId: client[0].id,
      roles: [
        {
          id: role.id,
          name: role.name,
        },
      ],
    });
  }
}
