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
    await fetch('http://localhost:3001/track', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
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
