import type { Command } from 'commander';
import stateUtils from '../../utils/state';
import telemetryUtils from '../../utils/telemetry';

/**
 * Common preAction hook to record telemetry for any command.
 * Builds a command path (parent name + args) and records it when telemetry is enabled.
 */
export const telemetryPreAction = async (thisCommand: Command) => {
  if (!stateUtils.isTelemetryEnabled()) return;
  const parent = thisCommand.parent;
  if (parent) {
    const parentName = parent.name();
    const parts: string[] = [parentName, ...parent.args];
    // Include current command name if it's different from parent name and not already first arg
    if (thisCommand.name() !== parentName && parts[1] !== thisCommand.name()) {
      parts.push(thisCommand.name());
    }
    await telemetryUtils.recordCommand(parts.join(' '));
  } else {
    await telemetryUtils.recordCommand(thisCommand.name());
  }
};
