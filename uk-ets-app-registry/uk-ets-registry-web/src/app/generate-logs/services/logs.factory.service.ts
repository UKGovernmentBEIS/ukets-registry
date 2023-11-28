import { Injectable, Injector } from '@angular/core';
import { v4 as uuidv4 } from 'uuid';
import {
  generatedLatestLogObject,
  Interaction,
} from '@generate-logs/actions/generate-logs.actions';
import {
  ApiRequestCorrelation,
  Log,
} from '@generate-logs/reducers/generate-logs.reducer';
import { Store } from '@ngrx/store';
import { dateTimeNowFormatted } from '@registry-web/shared/shared.util';

@Injectable({
  providedIn: 'root',
})
export class LogsFactoryService {
  //As LogsFactory Service is used in the Meta Reducer and we need a Store
  //Here to dispatch actions, this creates a cyclical dependency
  //The workaround is to use the injector to load the Store
  constructor(private injector: Injector) {}

  createInteractionObj(event: MouseEvent): Interaction {
    const elm = event.target as HTMLElement;
    const elmType = elm.nodeName === 'A' ? 'LINK' : elm.nodeName;
    const label = elm.innerText;
    const date = dateTimeNowFormatted();

    const interaction = {
      label: label,
      element: elmType,
      id: elm.id
        ? elm.id + Math.random().toString(36).substr(2, 9)
        : this.generateId(elmType, label),
      cause: 'user_action',
      date: date,
      action_identifier: uuidv4(),
    };

    if (elmType === 'LINK' || elmType === 'BUTTON' || elmType === 'INPUT') {
      // generate page step uuid based on target url
    }
    return interaction;
  }

  private generateId(type: string, label: string): string {
    // splits label when it consists by more than one words
    const arrayOfLabel = label ? label.split(' ') : [];
    let transformedLabel = '';

    // transforms first letter to capital
    for (let i = 0; i < arrayOfLabel.length; i++) {
      arrayOfLabel[i] =
        i > 0
          ? arrayOfLabel[i].charAt(0).toUpperCase() + arrayOfLabel[i].slice(1)
          : arrayOfLabel[i];
    }
    // camelCase naming convention
    transformedLabel = arrayOfLabel.length > 0 ? arrayOfLabel.join('') : label;

    return (
      transformedLabel +
      '-' +
      type.toLowerCase() +
      '-' +
      Math.random().toString(36).substr(2, 9)
    );
  }

  createLogObj(
    userAction: Interaction,
    userUuid: string,
    sessionId: string,
    userId: string
  ): Log {
    return LogsFactoryService.createLog(
      userAction,
      userUuid,
      sessionId,
      userId
    );
  }

  updateActionLog(log: Log) {
    this.store.dispatch(generatedLatestLogObject({ log }));
  }

  private get store() {
    return this.injector.get(Store);
  }

  updateInteractionLogs(
    logs: Log[],
    correlations: { [key: string]: ApiRequestCorrelation }
  ) {
    return logs.map((log) => {
      const correlation = correlations[log.id];
      if (correlation) {
        log = LogsFactoryService.updateInteraction(
          log,
          correlation.interaction
        );
      }
      return log;
    });
  }

  private static updateInteraction(log: Log, interaction: Interaction): Log {
    return {
      ...log,
      entrypoint: interaction.id,
      timestamp: interaction.date,
      cause: interaction.cause,
      user_action_identifier: interaction.action_identifier,
    };
  }

  private static createLog(
    userAction: Interaction,
    userUuid: string,
    sessionId: string,
    userId: string
  ) {
    const log: Log = Object.assign({});
    log.mdc = Object.assign({});
    log.mdc.app_name = 'uk-ets-registry-web';
    log.mdc.session_identifier = sessionId;
    log.mdc.ets_user_id = userUuid;
    log.mdc.user_id = userId;
    log.id = uuidv4();
    log.entrypoint = userAction.id;
    log.timestamp = userAction.date;
    log.cause = userAction.cause;
    log.user_action_identifier = userAction.action_identifier;
    log.exception = null;
    return log;
  }
}
