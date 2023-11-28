import { PathsAndTypesRegistryDb } from '../../paths-and-types-registry-db';
import { RegistryDbTask } from '../model/registry-db-task';
import { PostgrestClient } from '../../../postgrest-client.util';
import { environment } from '../../../../environment-configuration';

export class RegistryDbTaskTestData {
  static sequenceId = 100000001;

  static nextId() {
    return this.sequenceId++;
  }

  static resetIndices() {
    try {
      this.sequenceId = 100000001;
    } catch (e) {
      console.log(`Could not resetIndices.`);
    }
  }

  static async loadTasks(tasks: RegistryDbTask[]) {
    const createdTasks: RegistryDbTask[] = [];
    for (const task of tasks) {
      task.id = String(this.nextId());
      try {
        await PostgrestClient.genericLoad(
          environment().postgrestRegistryBaseUrl,
          PathsAndTypesRegistryDb.TASK_PATH,
          PathsAndTypesRegistryDb.TASK_TYPE,
          task
        );
        createdTasks.push(task);
      } catch (e) {
        console.error(`exception in test data: '${e}'.`);
      }
    }
    return createdTasks;
  }

  static async deleteAllTasksFromDB() {
    await PostgrestClient.genericRemove(
      environment().postgrestRegistryBaseUrl,
      PathsAndTypesRegistryDb.TASK_PATH,
      PathsAndTypesRegistryDb.TASK_TYPE
    );
  }
}
