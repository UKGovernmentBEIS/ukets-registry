export function indexOfItem(
  list: { requestId: string }[],
  item: { requestId: string }
): number {
  for (let i = 0; i < list.length; i++) {
    if (compare(list[i], item) === 0) {
      return i;
    }
  }
  return -1;
}

function compare(
  item1: { requestId: string },
  item2: { requestId: string }
): number {
  if (!!item1 && !!item2 && item1.requestId === item2.requestId) {
    return 0;
  }
  return -1;
}
