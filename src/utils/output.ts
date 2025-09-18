// Global output mode management (e.g., JSON vs human)
let outputState = { json: false } as { json: boolean };
export function setGlobalOutputMode(s: Partial<typeof outputState>) {
  outputState = { ...outputState, ...s };
}
export function isJsonOutput() {
  return outputState.json;
}

export function printOutput(human: string, data?: unknown) {
  if (outputState.json) {
    const payload = data !== undefined ? data : { message: human };
    // eslint-disable-next-line no-console
    console.log(JSON.stringify(payload, null, 2));
  } else {
    // eslint-disable-next-line no-console
    console.log(human);
  }
}
