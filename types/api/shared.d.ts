export interface Key {
  _type: string;
  key: string;
}

export interface Filter {
  field: string;
  operation: 'gt' | 'lt' | 'gte' | 'lte' | 'eq' | 'ne';
  value: number | string;
}