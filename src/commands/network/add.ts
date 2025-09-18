import { Command } from 'commander';
import { prompt } from 'enquirer';
import { getState, saveState } from '../../state/store';
import { color } from '../../utils/color';
import { exitOnError } from '../../utils/errors';
import { Logger } from '../../utils/logger';
import {
  checkMirrorNodeHealth,
  checkRpcHealth,
} from '../../utils/networkHealth';
import { telemetryPreAction } from '../shared/telemetryHook';

interface Answers {
  name: string;
  mirrorNodeUrl: string;
  rpcUrl: string;
  operatorId: string;
  operatorKey: string;
}

interface ConfirmSave {
  ok: boolean;
}
interface ConfirmActive {
  setActive: boolean;
}

const logger = Logger.getInstance();

export default (program: Command) => {
  program
    .command('add')
    .hook('preAction', telemetryPreAction)
    .description('Interactively add a custom network')
    .action(
      exitOnError(async () => {
        const answers: Answers = await prompt<Answers>([
          {
            type: 'input',
            name: 'name',
            message: 'Network name (e.g., solo-local):',
            validate: (v: string) =>
              v && v.trim().length > 0 ? true : 'Name is required',
          },
          {
            type: 'input',
            name: 'mirrorNodeUrl',
            message: 'Mirror Node URL (e.g., http://localhost:8081/api/v1):',
            validate: (v: string) =>
              /^https?:\/\//.test(v) ? true : 'Provide a valid http(s) URL',
          },
          {
            type: 'input',
            name: 'rpcUrl',
            message: 'RPC URL (e.g., http://localhost:7546):',
            validate: (v: string) =>
              /^https?:\/\//.test(v) ? true : 'Provide a valid http(s) URL',
          },
          {
            type: 'input',
            name: 'operatorId',
            message: 'Operator account ID (e.g., 0.0.2) [optional]:',
          },
          {
            type: 'input',
            name: 'operatorKey',
            message: 'Operator private key [optional]:',
          },
        ]);

        logger.log(color.cyan('Validating endpoints...'));
        const [mirrorStatus, rpcStatus] = await Promise.all([
          checkMirrorNodeHealth(answers.mirrorNodeUrl),
          checkRpcHealth(answers.rpcUrl),
        ]);

        logger.log(
          `Mirror Node: ${color.cyan(answers.mirrorNodeUrl)} ${mirrorStatus.status} ${
            mirrorStatus.code ? `(${mirrorStatus.code})` : ''
          }`,
        );
        logger.log(
          `RPC URL: ${color.cyan(answers.rpcUrl)} ${rpcStatus.status} ${
            rpcStatus.code ? `(${rpcStatus.code})` : ''
          }`,
        );

        const confirm = await prompt<ConfirmSave>({
          type: 'confirm',
          name: 'ok',
          message:
            'Save this network configuration even if health checks failed?',
          initial: mirrorStatus.status === '✅' && rpcStatus.status === '✅',
        });

        if (!confirm.ok) {
          logger.log(color.yellow('Aborted. Network not saved.'));
          return;
        }

        const state = getState();
        const name = answers.name.trim();
        const networks = { ...state.networks };
        networks[name] = {
          mirrorNodeUrl: answers.mirrorNodeUrl.trim(),
          rpcUrl: answers.rpcUrl.trim(),
          operatorId: answers.operatorId?.trim() || '',
          operatorKey: answers.operatorKey?.trim() || '',
          hexKey: '',
        };
        saveState({ networks });

        logger.log(color.green(`Network '${name}' saved.`));

        const setActive = await prompt<ConfirmActive>({
          type: 'confirm',
          name: 'setActive',
          message: 'Set this network as the active network?',
          initial: false,
        });

        if (setActive.setActive) {
          saveState({ network: name });
          logger.log(color.green(`Active network set to '${name}'.`));
        }
      }),
    );
};
