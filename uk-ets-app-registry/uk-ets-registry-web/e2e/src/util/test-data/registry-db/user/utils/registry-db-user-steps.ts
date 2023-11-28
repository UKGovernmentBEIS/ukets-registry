import { Given, TableDefinition, Then } from 'cucumber';
import { RegistryDbTestUser } from '../model/registry-db-test-user';
import { RegistryDbUserTestData } from './registry-db-user.util';

Given(
  'I have created {int} {string} users',
  async (num: number, state: string) => {
    let urid_idx: number;
    switch (state) {
      case 'registered': {
        urid_idx = 0;
        break;
      }
      case 'validated': {
        urid_idx = 10;
        break;
      }
      case 'enrolled': {
        urid_idx = 20;
        break;
      }
      case 'unenrolled': {
        urid_idx = 30;
        break;
      }
      case 'unenrollment pending': {
        urid_idx = 40;
        break;
      }
    }
    for (let counter = 0; counter < num; counter++) {
      const user = new RegistryDbTestUser();
      user.state = state.toUpperCase().replace(' ', '_');
      user.username = `${state.replace(' ', '_')}_user_${counter}`;
      user.firstName = state.toUpperCase().replace(' ', '_');
      user.lastName = `USER ${counter}`;
      user.email = `${state.replace(' ', '_')}_user_${counter}@test.com`;
      user.disclosedName = user.firstName + ' ' + user.lastName;
      await RegistryDbUserTestData.createUser(user, 0);
    }
  }
);

Given(
  'I have created {int} {string} administrators',
  async (num: number, role: string) => {
    console.log(
      `Entered step: I have created '${num}' '${role}' administrators.`
    );
    let urid_idx: number;
    let registryRoles: string[];
    switch (role) {
      case 'junior': {
        urid_idx = 100;
        registryRoles = ['junior-registry-administrator'];
        break;
      }
      case 'senior': {
        urid_idx = 110;
        registryRoles = ['senior-registry-administrator'];
        break;
      }
      case 'read only': {
        urid_idx = 120;
        registryRoles = ['readonly-administrator'];
        break;
      }
    }
    for (let counter = 0; counter < num; counter++) {
      console.log(`About to set new TestUser`);
      const user = new RegistryDbTestUser();
      user.state = 'ENROLLED';
      user.username = `${role.replace(' ', '_')}_admin_${counter}`;
      user.firstName = role.toUpperCase().replace(' ', '_');
      user.lastName = `ADMIN ${counter}`;
      user.email = `${role.replace(' ', '_')}_user_${counter}@test.com`;
      user.registryRoles = registryRoles;
      user.disclosedName = 'Registry Administrator';
      console.log(`About to access RegistryDbUserTestData createUser`);
      await Promise.all([RegistryDbUserTestData.createUser(user, 0)]);
    }
    console.log(
      `Exiting step: I have created '${num}' '${role}' administrators.`
    );
  }
);
