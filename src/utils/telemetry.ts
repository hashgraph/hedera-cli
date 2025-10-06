import { get as storeGet } from '../state/store';
// Use dynamic require but immediately narrow to expected shape to avoid unsafe any propagation
// eslint-disable-next-line @typescript-eslint/no-var-requires
const rawPkg = require('../../package.json') as unknown;
const version: string =
  typeof rawPkg === 'object' && rawPkg && 'version' in rawPkg
    ? String((rawPkg as { version: unknown }).version)
    : '0.0.0';

async function recordCommand(command: string) {
  const payload = {
    command: command,
    timestamp: new Date().toISOString(),
    version,
  };

  try {
    // TODO: Replace with actual telemetry endpoint.
    // If .env contains a TELEMETRY_URL, use that instead otherwise use the default URL.
    const telemetryServer = storeGet('telemetryServer');
    const telemetryUrl =
      (typeof telemetryServer === 'string' && telemetryServer !== ''
        ? telemetryServer
        : undefined) || 'https://hedera-cli-telemetry.onrender.com/track';
    await fetch(telemetryUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Telemetry-Token': ((): string => {
          const uuid = storeGet('uuid');
          return typeof uuid === 'string' && uuid !== ''
            ? uuid
            : 'facade00-0000-4000-a000-000000000000';
        })(),
      },
      body: JSON.stringify(payload),
    });
  } catch (err) {
    // Fail silently; telemetry errors should not impact user experience.
    console.error('Telemetry error:', err);
  }
}

// Flush any buffered telemetry; currently a no-op placeholder for future batching.
async function flush(): Promise<void> {
  // Intentionally empty – networking is awaited inline in recordCommand for now.
}

const telemetryUtils = {
  recordCommand,
  flush,
};

export default telemetryUtils;
