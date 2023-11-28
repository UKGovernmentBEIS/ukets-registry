import { KnowsThePage } from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheSpecifytheAccessRightsPage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.SPECIFY_THE_ACCESS_RIGHTS;
    this.setUpTestData();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }
}
