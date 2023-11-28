export function getRouteFromArray(commands: string[]): string {
  return `/${commands.join('/')}`;
}
