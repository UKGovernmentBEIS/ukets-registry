import { RegistryScreen } from './screens';

export class A11yViolation {
  description: string;
  help: string;
  helpUrl: string;
  id: 'string';
  impact: 'string';
  nodes: [];
  tags: [];

  static fromJSON(json: A11yViolation | string): A11yViolation {
    if (typeof json === 'string') {
      return JSON.parse(json, A11yViolation.reviver);
    } else if (!json) {
      return new A11yViolation();
    } else {
      return Object.assign(new A11yViolation(), json);
    }
  }

  static reviver(key: string, value: any): any {
    return key === '' ? A11yViolation.fromJSON(value) : value;
  }

  updateWithViolationOfSameType(violation: A11yViolation) {
    if (this.id === violation.id) {
      this.nodes.push(...violation.nodes);
    }
  }
}

export class A11yMatrix {
  private readonly matrix = new Map<RegistryScreen, A11yViolation[]>();

  update(screen: RegistryScreen, newViolations: any[]) {
    try {
      if (!this.matrix.has(screen)) {
        this.matrix.set(screen, []);
      }
      newViolations.forEach(newViolation => {
        const foundViolation = this.matrix
          .get(screen)
          .find(aViolation => aViolation.id === newViolation.id);
        if (foundViolation) {
          foundViolation.updateWithViolationOfSameType(
            A11yViolation.fromJSON(newViolation)
          );
        } else {
          this.matrix.get(screen).push(A11yViolation.fromJSON(newViolation));
        }
      });
    } catch (ex) {
      console.log(
        `Exception during 'update' function in 'A11yMatrix' class: '${ex}'.`
      );
    }
  }

  get() {
    return this.matrix;
  }

  print() {
    console.log('------------------------------------');
    console.log('h1. A11y Report');
    console.log('Rule-set: axe-core default ruleset');
    console.log(`Date: '${new Date().toISOString}'.`);
    this.get().forEach((value, key) => {
      console.log('h2. Screen: ' + key);
      console.log('h3. Violations: ' + value.length);
      value.forEach((violation, index) => {
        console.log(
          `${index + 1}. [${violation.id}|${violation.helpUrl}] [${
            violation.impact
          }]: ${violation.description}`
        );
      });
    });
    console.log('------------------------------------');
  }
}
