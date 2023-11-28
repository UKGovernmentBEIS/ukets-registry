export function updateEntryKey(map: any, oldKey: string, newKey: string) {
  console.log(
    `Entered updateEntryKey with: oldKey '${oldKey}' and newKey '${newKey}'.`
  );
  console.log(`Entries before updateEntryKey:`);
  printMapEntries(map);

  map.set(newKey, map.get(oldKey));
  map.delete(oldKey);
  console.log(`Entries after updateEntryKey:`);
  printMapEntries(map);
  console.log(`Exiting updateEntryKey`);
}

export function updateByKeyEntryValueAttribute(
  map: any,
  keyName: string,
  valueName: string,
  newAttributeValue: string
) {
  console.log(
    `Entered updateByKeyEntryValueAttribute with: keyName '${keyName}', valueName '${valueName}', newAttributeValue '${newAttributeValue}'`
  );
  console.log(`Entries before updateByKeyEntryValueAttribute:`);
  printMapEntries(map);

  for (const entry of map.entries()) {
    if (entry[0] === keyName && valueName === 'secret') {
      map.delete(entry);
      map.set(keyName, { secret: newAttributeValue, otp: entry[1]['otp'] });
    } else if (entry[0] === keyName && valueName === 'otp') {
      map.delete(entry);
      map.set(keyName, { secret: entry[1]['secret'], otp: newAttributeValue });
    } else {
      // no change, or extend this function.
    }
  }
  console.log(`Entries after updateByKeyEntryValueAttribute:`);
  printMapEntries(map);
}

export function hasEntryInMap(map: any, entryKey: string) {
  console.log(`Entered hasEntryInMap with entryKey '${entryKey}'.`);
  const value = map.has(entryKey);
  console.log(`Exiting hasEntryInMap with returning value '${value}'.\n\n`);
  return value;
}

export function addEntryToMap(map: any, entryKey: string, entryValue: any) {
  console.log(
    `Entered addEntryToMap with entryKey '${entryKey}' and entryValue: secret '${entryValue['secret']}, otp '${entryValue['otp']}'.` +
      ` Entries before addEntryToMap:`
  );
  printMapEntries(map);
  map.set(entryKey, entryValue);
  console.log(`Exiting addEntryToMap. Entries after addEntryToMap:`);
  printMapEntries(map);
}

export function deleteMapEntry(map: any, deleteKey: string) {
  console.log(`\n\nEntered deleteMapEntry. Entries before deleteMapEntry:`);
  printMapEntries(map);
  console.log(`Deleting entry '${deleteKey}' by key name.`);
  map.delete(deleteKey);
  console.log(`Exiting deleteMapEntry Entries after deleteMapEntry: `);
  printMapEntries(map);
}

export function deleteAllMapEntries(map: any) {
  console.log(`\n\nEntered deleteAllMapEntries. Entries before:`);
  printMapEntries(map);
  map.clear();
  console.log(`Exiting deleteAllMapEntries Entries after:`);
  printMapEntries(map);
}

export function printMapEntries(map: any) {
  if (map === null) {
    console.log(
      `Null argument in printMapEntries. No print elements available.`
    );
  } else {
    for (const entry of map.entries()) {
      console.log(
        `printMapEntries > Entry: '${entry[0]}', secret: '${entry[1]['secret']}', otp: '${entry[1]['otp']}'.`
      );
    }
  }
}

export function getMapEntryValueAttributeByKey(
  map: any,
  key: string,
  attribute: 'otp' | 'secret'
) {
  console.log(
    `\nEntered getMapEntryValueAttributeByKey with key '${key}' and attribute '${attribute}'.`
  );
  let returnValue = '';
  printMapEntries(map);
  if (map === null) {
    console.log(`Null map, empty value is returned.`);
  } else {
    for (const entry of map.entries()) {
      if (entry[0] === key) {
        returnValue = entry[1][attribute];
        break;
      }
    }
  }

  console.log(
    `Exiting getMapEntryValueAttributeByKey with returnValue '${returnValue}'.\n`
  );
  return returnValue;
}

export function printMapKeys(map: any) {
  console.log(`\n\nEntered printMapKeys`);
  for (const key of map.keys()) {
    console.log(key);
  }
  console.log(`Exiting printMapKeys`);
}

export function printMapValues(map: any) {
  console.log(`Entered printMapValues`);
  for (const value of map.values()) {
    console.log(value);
  }
  console.log(`Exiting printMapValues`);
}
