import { KnowsThePage } from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheHomePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.HOME;
    this.setUpTestData();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }
}
