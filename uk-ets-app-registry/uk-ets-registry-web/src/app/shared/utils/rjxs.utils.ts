import { Observable, timer } from 'rxjs';
import { concatMap, retryWhen } from 'rxjs/operators';

export function retryBackoff(
  config: {
    maxRetries: number;
    initialDelay: number;
    onFailure?: () => void;
  } = {
    maxRetries: 5,
    initialDelay: 30000,
  }
): <T>(source: Observable<T>) => Observable<T> {
  return <T>(source: Observable<T>) =>
    source.pipe(
      retryWhen((errors) => {
        return errors.pipe(
          concatMap((error, i) => {
            if (i < config.maxRetries - 1) {
              const delay = Math.pow(2, i) * config.initialDelay;
              return timer(delay);
            } else {
              config.onFailure();
            }
          })
        );
      })
    );
}
