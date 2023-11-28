import { ACTION, Messages, OUTCOME } from './messages';
import * as superagent from 'superagent';
import { GlobalState } from '../../step-definitions/ui-step-definitions';

export class PostgrestClient {
  public static async genericUpdate(
    baseUri: string,
    path: string,
    type: string,
    entity: any,
    idField?: string
  ) {
    console.log(`genericUpdate: baseUri: '${baseUri}', path: '${path}', type: '${type}', entity: '${entity}'.`);
    let hasError = false;
    let fullPath = '';
    try {
      fullPath = `${baseUri}${path}`;
      await superagent.patch(fullPath).send(entity);
      const message = Messages.getGenericActionMessage(
        entity[idField ? idField : 'id'],
        type,
        ACTION.INSERT,
        OUTCOME.SUCCESS,
        null
      );
      console.log(`genericUpdate: '${fullPath}': message: '${message}'.`);
      if (message.includes(`Error: `) || message.includes(`Bad Request`)) {
        console.error(`error '${fullPath}' genericUpdate, '${message}'.`);
      }
    } catch (e) {
      GlobalState.hasScenarioTestDataError = true;
      hasError = true;
       console.error(
       `\ngenericUpdate: '${fullPath}': Test data error: '${e}'.\n` +
         `message: '${e.message}'.\n` +
          Messages.getGenericActionMessage(
            entity[idField ? idField : 'id'],
            type,
            ACTION.INSERT,
            OUTCOME.FAILURE,
            e.message
          )
       );
      Messages.getGenericActionMessage(
        entity[idField ? idField : 'id'],
        type,
        ACTION.INSERT,
        OUTCOME.FAILURE,
        e.message
      );
      console.error(e);
    } finally {
      if (hasError) {
        console.error(`Error found.\n`);
      }
    }
  }

  public static async genericLoad(
    baseUri: string,
    path: string,
    type: string,
    entity: any,
    idField?: string
  ) {
    console.log(`genericLoad: baseUri: '${baseUri}', path: '${path}', type: '${type}', entity: '${entity}'.`);
    let hasError = false;
    let fullPath = '';
    try {
      const fullPath = `${baseUri}${path}`;
      await superagent.post(fullPath).send(entity);
      const message = Messages.getGenericActionMessage(
        entity[idField ? idField : 'id'],
        type,
        ACTION.INSERT,
        OUTCOME.SUCCESS,
        null
      );
      console.log(`genericLoad: '${fullPath}': '${message}'.`);
      if (message.includes(`Error: `) || message.includes(`Bad Request`)) {
        console.error(
          `error '${fullPath}' genericLoad, response contains error.`
        );
      }
    } catch (e) {
      GlobalState.hasScenarioTestDataError = true;
      hasError = true;
       console.error(
        `\ngenericLoad: '${fullPath}': Test data error: '${e}'.\n` +
          `message: '${e.message}'.\n` +
          Messages.getGenericActionMessage(
            entity[idField ? idField : 'id'],
            type,
            ACTION.INSERT,
            OUTCOME.FAILURE,
            e.message
          )
       );
      console.error(e);
    } finally {
      if (hasError) {
        console.error(`Error found.\n`);
      }
    }
  }

  // genericRemove
  public static async genericRemove(
    baseUri: string,
    path: string,
    type: string,
    id?: string,
    gte?: boolean,
    idField?: string
  ) {
    console.log(`genericRemove: baseUri: '${baseUri}', path: '${path}', type: '${type}'.`);
    if (id) {
      await this.genericDelete(baseUri, path, type, id, gte, idField);
    } else {
      await this.genericDelete(baseUri, path, type);
    }
  }

  // genericDelete using DEL
  public static async genericDelete(
    baseUri: string,
    path: string,
    type: string,
    id?: string,
    gte?: boolean,
    idField?: string
  ) {
    console.log(`genericDelete: baseUri: '${baseUri}', path: '${path}', type: '${type}'.`);
    let hasError = false;
    let fullPath = '';
    try {
      fullPath = id
        ? `${path}?${idField ? idField : 'id'}=${gte ? 'gte' : 'eq'}.${id}`
        : path;
      await superagent.del(`${baseUri}${fullPath}`);

      const message = Messages.getGenericActionMessage(
        id ? String(id) : null,
        type,
        ACTION.DELETE,
        OUTCOME.SUCCESS,
        null
      );
      console.log(`genericDelete: '${baseUri}${fullPath}': '${message}'.`);
      if (message.includes(`Error: `) || message.includes(`Bad Request`)) {
        console.error(`error '${baseUri}${fullPath}' inBaseUrl genericDelete.`);
      }
    } catch (e) {
      GlobalState.hasScenarioTestDataError = true;
      hasError = true;
       console.error(
        `\ngenericDelete: '${baseUri}${fullPath}': Test data error: '${e}'.\n` +
          `message: '${e.message}', baseUri: '${baseUri}', path: '${path}', type: '${type}'.\n` +
          Messages.getGenericActionMessage(
            id ? String(id) : null,
            type,
            ACTION.DELETE,
            OUTCOME.FAILURE,
            e.message
          )
       );
      console.error(e);
    } finally {
      if (hasError) {
        console.error(`Error found.\n`);
      }
    }
  }
}
