import { Given, TableDefinition } from 'cucumber';
import { RegistryDbTaskBuilder } from '../model/registry-db-task.builder';
import moment from 'moment';
import { RegistryDbTaskTestData } from './registry-db-task.util';
import { assert } from 'chai';

Given(
  'I have created the following tasks',
  async (dataTable: TableDefinition) => {
    console.log('Executing step: I have created the following tasks');
    const rows: string[][] = dataTable.raw();
    let requestId = 100000000;
    for (const row of rows) {
      if (row[0] === 'account_id') {
        continue;
      }
      const accountId = row[0] === '' ? null : row[0];
      const claimedBy = row[1] === '' ? null : row[1];
      const status = row[2] === '' ? null : row[2];
      const outcome = row[3] === '' ? null : row[3];
      const type = row[4] === '' ? null : row[4];
      const initiatedBy = row[5] === '' ? null : row[5];
      const completedBy = row[6] === '' ? null : row[6];
      const diff_field = row[7] === '' ? null : row[7];

      const transactionIdentifier = row[8] === '' ? null : row[8];
      const recipientAccountNumber = row[9] === '' ? null : row[9];

      //Task id will increment for each task created starting at 100000001
      requestId = requestId + 1;

      ///////////////////////////////////////////////////////////////////////////////
      // "diff" column at Gherkin level corresponds to json field "difference".
      // This field needed is converted to json format at code level, not BDD level,
      // so as not to include json-like content to Gherkin files
      //
      // Case 1: If "diff" should correspond to the "TAL IDs for the specific task", it should be used as below:
      // It can contain one value for ADD tal, it has at least value for REMOVE tal.
      // example values: {"ids":[89]}, {"ids":[89, 84]}
      let diff: any;
      let before: any;
      let diff_elements: any;
      // console.log(`diff_field is ${diff_field}`);
      // Case: If "diff" column does not exist at Gherkin level, set diff value to null.
      if (
        diff_field === undefined ||
        diff_field === null ||
        diff_field === ''
      ) {
        diff = null;
      } else if (diff_field.includes('tal ids')) {
        diff = `{"ids":[` + diff_field.replace('tal ids:', '') + `]}`;
      }
      // Case: If "diff" should correspond to ADD authorized representative, it should be used as below:
      // example values: {"type":"ADD","urid":"UK88299344979","accountAccessRight":"INITIATE_AND_APPROVE"}
      else if (diff_field.includes('auth rep:ADD')) {
        diff_elements = diff_field.replace('auth rep:', '').split(',');
        diff = `{"type":"${diff_elements[0]}","urid":"${diff_elements[1]}","accountAccessRight":"${diff_elements[2]}"}`;
      }
      // Case: If "diff" should correspond to REPLACE authorized representative, it should be used as below:
      // example values: {"type":"REPLACE","urid":"UK498099973231","toBeReplacedUrid":"UK405681794859","accountAccessRight":"APPROVE"}
      else if (diff_field.includes('auth rep:REPLACE')) {
        diff_elements = diff_field.replace('auth rep:', '').split(',');
        diff =
          `{"type":"${diff_elements[0]}","urid":"${diff_elements[1]}",` +
          `"toBeReplacedUrid":"${diff_elements[2]}","accountAccessRight":"${diff_elements[3]}"}`;
      }
      // Case: If "diff" should correspond to REMOVE authorized representative, it should be used as below:
      // example values: {"type":"REMOVE","urid":"UK566650437068"}
      else if (diff_field.includes('auth rep:REMOVE')) {
        diff_elements = diff_field.replace('auth rep:', '').split(',');
        diff = `{"type":"${diff_elements[0]}","urid":"${diff_elements[1]}"}`;
      }
      // Case: If "diff" should correspond to RESTORE authorized representative, it should be used as below:
      // example values: {"type":"RESTORE","urid":"UK813935774586"}
      else if (diff_field.includes('auth rep:RESTORE')) {
        diff_elements = diff_field.replace('auth rep:', '').split(',');
        diff = `{"type":"${diff_elements[0]}","urid":"${diff_elements[1]}"}`;
      }
      // Case: If "diff" should correspond to CHANGE_ACCESS_RIGHTS authorized representative, it should be used as below:
      // example values: {"type":"CHANGE_ACCESS_RIGHTS","urid":"UK813935774586","accountAccessRight":"APPROVE"}
      else if (diff_field.includes('auth rep:CHANGE_ACCESS_RIGHTS')) {
        diff_elements = diff_field.replace('auth rep:', '').split(',');
        diff = `{"type":"${diff_elements[0]}","urid":"${diff_elements[1]}","accountAccessRight":"${diff_elements[2]}"}`;
      }
      // Case: If "diff" should correspond to SUSPEND authorized representative, it should be used as below:
      // example values: {"type":"SUSPEND","urid":"UK813935774586"}
      else if (diff_field.includes('auth rep:SUSPEND')) {
        diff_elements = diff_field.replace('auth rep:', '').split(',');
        diff = `{"type":"${diff_elements[0]}","urid":"${diff_elements[1]}"}`;
      } else if (diff_field.includes('ACCOUNT_OPENING_REQUEST')) {
        diff = `{"identifier":null,"accountType":"OPERATOR_HOLDING_ACCOUNT","accountHolder":{"id":null,"emailAddress":null,"address":{"country":"UK","postCode":"15151","empty":false,"townOrCity":"org1","buildingAndStreet":"org1","buildingAndStreet2":"","buildingAndStreet3":""},"phoneNumber":null,"details":{"name":"org","firstName":null,"lastName":null,"registrationNumber":"","noVatJustification":null,"regNumTypeRadio":null,"noRegistrationNumJustification":"1","countryOfBirth":null,"yearOfBirth":null,"birthDate":null},"type":"ORGANISATION","vat":{"vatRegistrationNumber":"","noVatJustification":"1","vatRegNumTypeRadio":3},"status":null},"oldAccountHolder":{"id":null,"emailAddress":null,"address":{"country":"UK","postCode":"15151","empty":false,"townOrCity":"org1","buildingAndStreet":"org1","buildingAndStreet2":"","buildingAndStreet3":""},"phoneNumber":null,"details":{"name":"org","firstName":null,"lastName":null,"registrationNumber":"","noVatJustification":null,"regNumTypeRadio":null,"noRegistrationNumJustification":"1","countryOfBirth":null,"yearOfBirth":null,"birthDate":null},"type":"ORGANISATION","vat":{"vatRegistrationNumber":"","noVatJustification":"1","vatRegNumTypeRadio":3},"status":null},"legalRepresentatives":null,"accountHolderContactInfo":{"primaryContact":{"id":null,"details":{"firstName":"org1","lastName":"org1","aka":"org1 org1","yearOfBirth":null,"birthDate":null},"positionInCompany":"org1","address":{"country":"UK","postCode":"15151","empty":false,"townOrCity":"org1","buildingAndStreet":"org1","buildingAndStreet2":"","buildingAndStreet3":""},"phoneNumber":{"phoneNumber1":"2310521010","phoneNumber2":"","countryCode1":"GR (30)","countryCode2":"","empty":false},"emailAddress":{"emailAddress":"org1@org1.org","emailAddressConfirmation":"org1@org1.org","empty":false}},"alternativeContact":null},"oldAccountHolderContactInfo":{"primaryContact":{"id":null,"details":{"firstName":"org1","lastName":"org1","aka":"org1 org1","yearOfBirth":null,"birthDate":null},"positionInCompany":"org1","address":{"country":"UK","postCode":"15151","empty":false,"townOrCity":"org1","buildingAndStreet":"org1","buildingAndStreet2":"","buildingAndStreet3":""},"phoneNumber":{"phoneNumber1":"2310521010","phoneNumber2":"","countryCode1":"GR (30)","countryCode2":"","empty":false},"emailAddress":{"emailAddress":"org1@org1.org","emailAddressConfirmation":"org1@org1.org","empty":false}},"alternativeContact":null},"accountDetails":{"name":"org1","accountType":null,"accountNumber":null,"accountHolderName":null,"accountStatus":null,"address":{"country":"UK","postCode":"","empty":false,"townOrCity":"","buildingAndStreet":"","buildingAndStreet2":"","buildingAndStreet3":""}},"accountDetailsSameBillingAddress":false,"trustedAccountListRules":{"rule1":false,"rule2":false},"operator":{"type":"INSTALLATION","identifier":null,"name":"org1","regulator":"EA","changedRegulator":null,"firstYear":2021,"lastYear":null,"lastYearChanged":null,"activityType":"COMBUSTION_OF_FUELS","permit":{"id":"1","date":{"day":1,"month":1,"year":2021}},"monitoringPlan":null},"installationToBeTransferred":null,"authorisedRepresentatives":[{"right":"INITIATE_AND_APPROVE","urid":"UK88299344979","firstName":"Authorised","lastName":"Representative1"},{"right":"INITIATE_AND_APPROVE","urid":"UK689820232063","firstName":"Authorised","lastName":"Representative2"},{"right":"INITIATE_AND_APPROVE","urid":"UK844883074633","firstName":"Authorised","lastName":"Representative3"},{"right":"INITIATE_AND_APPROVE","urid":"UK309464690132","firstName":"Authorised","lastName":"Representative4"},{"right":"INITIATE_AND_APPROVE","urid":"UK908452725792","firstName":"Authorised","lastName":"Representative5"},{"right":"INITIATE_AND_APPROVE","urid":"UK353782343224","firstName":"Authorised","lastName":"Representative6"}],"changedAccountHolderId":null,"oldAccountStatus":null,"balance":null,"unitType":null,"governmentAccount":false,"trustedAccountList":null,"transactionsAllowed":false,"pendingARRequests":null}`;
      } else if (diff_field.includes('AIRCRAFT_OPERATOR_HOLDING_ACCOUNT')) {
        diff = `{"identifier":null,"accountType":"AIRCRAFT_OPERATOR_HOLDING_ACCOUNT","accountHolder":{"id":null,"emailAddress":null,"address":{"country":"AE","postCode":"","empty":false,"townOrCity":"Town or city","buildingAndStreet":"Address line 1","buildingAndStreet2":"","buildingAndStreet3":""},"phoneNumber":null,"details":{"name":"org1","firstName":null,"lastName":null,"registrationNumber":"12345","noVatJustification":null,"regNumTypeRadio":null,"noRegistrationNumJustification":"","countryOfBirth":null,"yearOfBirth":null,"birthDate":null},"type":"ORGANISATION","vat":{"vatRegistrationNumber":"43321","noVatJustification":"","vatRegNumTypeRadio":2},"status":null},"oldAccountHolder":{"id":null,"emailAddress":null,"address":{"country":"AE","postCode":"","empty":false,"townOrCity":"Town or city","buildingAndStreet":"Address line 1","buildingAndStreet2":"","buildingAndStreet3":""},"phoneNumber":null,"details":{"name":"org1","firstName":null,"lastName":null,"registrationNumber":"12345","noVatJustification":null,"regNumTypeRadio":null,"noRegistrationNumJustification":"","countryOfBirth":null,"yearOfBirth":null,"birthDate":null},"type":"ORGANISATION","vat":{"vatRegistrationNumber":"43321","noVatJustification":"","vatRegNumTypeRadio":2},"status":null},"legalRepresentatives":null,"accountHolderContactInfo":{"primaryContact":{"id":null,"details":{"firstName":"test name","lastName":"Family name","aka":"test name Family name","yearOfBirth":null,"birthDate":null},"positionInCompany":"tester","address":{"country":"AE","postCode":"","empty":false,"townOrCity":"Town or city","buildingAndStreet":"Address line 1","buildingAndStreet2":"","buildingAndStreet3":""},"phoneNumber":{"phoneNumber1":"6999999999","phoneNumber2":"","countryCode1":"GR (30)","countryCode2":"","empty":false},"emailAddress":{"emailAddress":"a@a.com","emailAddressConfirmation":"a@a.com","empty":false}},"alternativeContact":null},"oldAccountHolderContactInfo":{"primaryContact":{"id":null,"details":{"firstName":"test name","lastName":"Family name","aka":"test name Family name","yearOfBirth":null,"birthDate":null},"positionInCompany":"tester","address":{"country":"AE","postCode":"","empty":false,"townOrCity":"Town or city","buildingAndStreet":"Address line 1","buildingAndStreet2":"","buildingAndStreet3":""},"phoneNumber":{"phoneNumber1":"6999999999","phoneNumber2":"","countryCode1":"GR (30)","countryCode2":"","empty":false},"emailAddress":{"emailAddress":"a@a.com","emailAddressConfirmation":"a@a.com","empty":false}},"alternativeContact":null},"accountDetails":{"name":"ETS - Aircraft Operator Holding Account","accountType":null,"accountNumber":null,"accountHolderName":null,"accountStatus":null,"address":{"country":"UK","postCode":"","empty":false,"townOrCity":"","buildingAndStreet":"","buildingAndStreet2":"","buildingAndStreet3":""}},"accountDetailsSameBillingAddress":false,"trustedAccountListRules":{"rule1":true,"rule2":false},"operator":{"type":"AIRCRAFT_OPERATOR","identifier":null,"name":null,"regulator":"EA","changedRegulator":null,"firstYear":2021,"lastYear":null,"lastYearChanged":null,"activityType":null,"permit":null,"monitoringPlan":{"id":"Monitoring plan ID 1"}},"installationToBeTransferred":null,"authorisedRepresentatives":[],"changedAccountHolderId":null,"oldAccountStatus":null,"balance":null,"unitType":null,"governmentAccount":false,"trustedAccountList":null,"transactionsAllowed":false,"pendingARRequests":null}`;
      }

      // account holder update: it can be a. either ACCOUNT_HOLDER_UPDATE_DETAILS or ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS,
      // and b. either for ORGANISATION or INDIVIDUAL
      // parameterize current before and diff values below in case of further arguments used
      else if (
        diff_field.includes('ACCOUNT_HOLDER_UPDATE_DETAILS:INDIVIDUAL')
      ) {
        diff_elements = diff_field
          .replace('ACCOUNT_HOLDER_UPDATE_DETAILS:INDIVIDUAL', '')
          .split(',');
        before = `{"id":100001,"emailAddress":{"emailAddress":"dont@care.com","emailAddressConfirmation":"dont@care.com","empty":false},"address":{"country":"UK","postCode":"12345","empty":false,"townOrCity":"London","buildingAndStreet":"Test address 7","buildingAndStreet2":"Second address line","buildingAndStreet3":"Third address line"},"phoneNumber":{"phoneNumber1":"1434634996","phoneNumber2":"1434634997","countryCode1":"44","countryCode2":"44","empty":false},"details":{"name":"holderName 1","registrationNumber":"UK1234567890","regNumTypeRadio":0,"countryOfBirth":"UK","birthDate":{"day":"01","month":"01","year":"1986"}},"type":"INDIVIDUAL","vat":{"vatRegistrationNumber":"123-456-789-0","vatRegNumTypeRadio":2},"status":"ACTIVE"}`;
        diff = `{"emailAddress":{"emailAddress":"email2@email2.com","emailAddressConfirmation":"email2@email2.com","empty":false},"address":{"postCode":"67890","empty":false,"townOrCity":"Barcelona","buildingAndStreet":"Test address 9","buildingAndStreet2":"Second address line 3","buildingAndStreet3":"Third address line 2"},"phoneNumber":{"empty":true},"details":{"name":"holderName 2","birthDate":{"day":"02","month":"03","year":"1988"}},"vat":{}}`;
      } else if (
        diff_field.includes('ACCOUNT_HOLDER_UPDATE_DETAILS:ORGANISATION')
      ) {
        diff_elements = diff_field
          .replace('ACCOUNT_HOLDER_UPDATE_DETAILS:ORGANISATION', '')
          .split(',');
        before = `{"id":100001,"emailAddress":{"emailAddress":"dont@care.com","emailAddressConfirmation":"dont@care.com","empty":false},"address":{"country":"UK","postCode":"12345","empty":false,"townOrCity":"London","buildingAndStreet":"Test address 7","buildingAndStreet2":"Second address line","buildingAndStreet3":"Third address line"},"phoneNumber":{"phoneNumber1":"1434634996","phoneNumber2":"1434634997","countryCode1":"44","countryCode2":"44","empty":false},"details":{"name":"holderName 1","registrationNumber":"UK1234567890","regNumTypeRadio":0,"countryOfBirth":"UK","birthDate":{"day":"01","month":"01","year":"1986"}},"type":"ORGANISATION","vat":{"vatRegistrationNumber":"123-456-789-0","vatRegNumTypeRadio":2},"status":"ACTIVE"}`;
        diff = `{"emailAddress":{"empty":true},"address":{"postCode":"67890","empty":false,"townOrCity":"Barcelona","buildingAndStreet":"Test address 9","buildingAndStreet2":"Second address line 2","buildingAndStreet3":"Third address line 2"},"phoneNumber":{"empty":true},"details":{"name":"holderName 2","registrationNumber":"","noRegistrationNumJustification":"Company reason 2"},"vat":{"vatRegistrationNumber":"","noVatJustification":"VAT reason 1","vatRegNumTypeRadio":3}}`;
      } else if (
        diff_field.includes('ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS:INDIVIDUAL')
      ) {
        diff_elements = diff_field
          .replace('ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS:INDIVIDUAL', '')
          .split(',');
        before = `{"id":100000001,"details":{"firstName":"AccountHolder","lastName":"Rep1","aka":"AKA 1","birthDate":{"day":"01","month":"01","year":"1987"}},"address":{"country":"UK","postCode":"12345","empty":false,"townOrCity":"London","buildingAndStreet":"Test address 7","buildingAndStreet2":"Second address line","buildingAndStreet3":"Third address line"},"phoneNumber":{"phoneNumber1":"1434634996","phoneNumber2":"1434634997","countryCode1":"44","countryCode2":"44","empty":false},"emailAddress":{"emailAddress":"dont@care.com","emailAddressConfirmation":"dont@care.com","empty":false}}`;
        diff = `{"positionInCompany":"company position 3","details":{"firstName":"AccountHolder 3","lastName":"second last name","aka":"AKA 2"},"address":{"empty":false,"buildingAndStreet":"Test address 8","buildingAndStreet2":"Second address line 4"},"phoneNumber":{"empty":true},"emailAddress":{"empty":true}}`;
      } else if (
        diff_field.includes(
          'ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS:ORGANISATION'
        )
      ) {
        diff_elements = diff_field
          .replace('ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS:ORGANISATION', '')
          .split(',');
        before = `{"id":100000001,"details":{"firstName":"AccountHolder","lastName":"Rep1","aka":"AKA 1","birthDate":{"day":"01","month":"01","year":"1987"}},"address":{"country":"UK","postCode":"12345","empty":false,"townOrCity":"London","buildingAndStreet":"Test address 7","buildingAndStreet2":"Second address line","buildingAndStreet3":"Third address line"},"phoneNumber":{"phoneNumber1":"1434634996","phoneNumber2":"1434634997","countryCode1":"44","countryCode2":"44","empty":false},"emailAddress":{"emailAddress":"dont@care.com","emailAddressConfirmation":"dont@care.com","empty":false}}`;
        diff = `{"positionInCompany":"company position 3","details":{"firstName":"AccountHolder 3","lastName":"second last name","aka":"AKA 2"},"address":{"empty":false,"buildingAndStreet":"Test address 8","buildingAndStreet2":"Second address line 4"},"phoneNumber":{"empty":true},"emailAddress":{"empty":true}}`;
      } else {
        assert.fail(
          `In step 'I have created the following tasks', use valid arguments or extend step definition.`
        );
      }
      ///////////////////////////////////////////////////////////////////////////////

      const initiatedDate = new Date();
      initiatedDate.setDate(initiatedDate.getDate());

      // completed date
      let completedDate: any;
      if (completedBy !== null) {
        completedDate = new Date();
      }

      // claimed date
      let claimedDate: any;
      if (claimedBy !== null) {
        claimedDate = new Date();
        claimedDate.setDate(claimedDate.getDate());
      }

      let task: any;
      let taskBuilder: any;
      taskBuilder = new RegistryDbTaskBuilder();
      taskBuilder
        .account_id(accountId)
        .request_identifier(requestId)
        .difference(diff)
        .before(before)
        .claimed_by(claimedBy)
        .status(status)
        .outcome(outcome)
        .type(type)
        .initiated_by(initiatedBy)
        .completed_by(completedBy)
        .initiated_date(moment(initiatedDate).format('YYYY-MM-DD HH:mm:ss'));

      // transactionIdentifier
      if (transactionIdentifier !== null) {
        taskBuilder.transaction_identifier(transactionIdentifier);
      }

      // recipientAccountNumber
      if (transactionIdentifier !== null) {
        taskBuilder.recipient_account_number(recipientAccountNumber);
      }

      // claimed_date
      if (claimedBy !== null) {
        taskBuilder.claimed_date(
          moment(claimedDate).format('YYYY-MM-DD HH:mm:ss')
        );
      }

      // completed_date
      if (completedBy !== null) {
        taskBuilder.completed_date(
          moment(completedDate).format('YYYY-MM-DD HH:mm:ss')
        );
      }

      task = taskBuilder.build();
      await RegistryDbTaskTestData.loadTasks([task]);
    }
  }
);
