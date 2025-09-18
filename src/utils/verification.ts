export function myParseInt(value: string): number {
  const parsedValue = Number.parseInt(value, 10);
  if (Number.isNaN(parsedValue)) {
    throw new Error('Expected an integer');
  }
  return parsedValue;
}

// Alias for future extension / clarity in command option parsing
export const parseIntOption = myParseInt;
