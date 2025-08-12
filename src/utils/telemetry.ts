import { get as storeGet } from '../state/store';
const { version } = require('../../package.json');

async function recordCommand(command: string) {
  const payload = {
    command: command,
    timestamp: new Date().toISOString(),
    version,
  };

  try {
    // TODO: Replace with actual telemetry endpoint.
    // If .env contains a TELEMETRY_URL, use that instead otherwise use the default URL.
    const telemetryUrl =
      (storeGet('telemetryServer' as any) as any) ||
      'https://hedera-cli-telemetry.onrender.com/track';
    await fetch(telemetryUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Telemetry-Token':
          (storeGet('uuid' as any) as any) ||
          'facade00-0000-4000-a000-000000000000', // Default user ID
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
