import stateController from '../state/stateController';
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
      stateController.get('telemetryServer') ||
      'https://hedera-cli-telemetry.onrender.com/track';
    await fetch(telemetryUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Telemetry-Token':
          stateController.get('uuid') || 'facade00-0000-4000-a000-000000000000', // Default user ID
      },
      body: JSON.stringify(payload),
    });
  } catch (err) {
    // Fail silently; telemetry errors should not impact user experience.
    console.error('Telemetry error:', err);
  }
}

const telemetryUtils = {
  recordCommand,
};

export default telemetryUtils;
