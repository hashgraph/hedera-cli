import dynamicVariablesUtils from '../../utils/dynamicVariables';
import { exitOnError } from '../../utils/errors';
import { Logger } from '../../utils/logger';

const logger = Logger.getInstance();

export interface WrapConfig<T> {
  /** Optional verbose log (string or function) executed after option replacement */
  log?: string | ((opts: T) => string);
}

/**
 * Wrap a command action to automatically:
 * 1. Replace dynamic variables in options
 * 2. Emit a verbose log (optional)
 * 3. Apply standard error handling via exitOnError
 */
export function wrapAction<T extends Record<string, any>>( // eslint-disable-line @typescript-eslint/no-explicit-any
  handler: (opts: T) => Promise<void> | void,
  config?: WrapConfig<T>,
) {
  return exitOnError(async (opts: T) => {
    const replaced = dynamicVariablesUtils.replaceOptions(opts);
    if (config?.log) {
      const msg =
        typeof config.log === 'function' ? config.log(replaced) : config.log;
      logger.verbose(msg);
    }
    await handler(replaced);
  });
}
