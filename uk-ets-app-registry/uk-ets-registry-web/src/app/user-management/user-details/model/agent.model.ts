export type AgentType = 'YES_PUBLIC' | 'YES_PRIVATE' | 'NO';

export const AGENT_TYPE_LABELS: Record<
  AgentType,
  { label: string; description?: string }
> = {
  YES_PUBLIC: { label: 'Yes - Public' },
  YES_PRIVATE: { label: 'Yes - Private' },
  NO: { label: 'No' },
};
