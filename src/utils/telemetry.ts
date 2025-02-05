//

function recordCommand(command: string) {
  console.log(`Command: ${command} was executed`);
}

const telemetryUtils = {
  recordCommand,
};

export default telemetryUtils;
