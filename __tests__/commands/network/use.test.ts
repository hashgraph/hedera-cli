import { baseState } from '../../helpers/state';
import { Command } from 'commander';
import commands from '../../../src/commands';
import { saveState as storeSaveState, get as storeGet } from '../../../src/state/store';

describe('network use command', () => {

  beforeEach(() => {
    const stateCopy = {
      ...baseState,
      // Provide a bogus mainnet operator ID and key
      mainnetOperatorId: '0.0.1001',
      mainnetOperatorKey:
        '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
    };

  storeSaveState(stateCopy as any);
  });

  describe('network use - success path', () => {
  afterEach(() => {});

    test('✅ switch to mainnet', async () => {
      // Assert
  expect(storeGet('network' as any)).toEqual('localnet');

      // Arrange
      const program = new Command();
      commands.networkCommands(program);

      // Act
      await program.parseAsync(['node', 'hedera-cli.ts', 'network', 'use', 'mainnet']);

      // Assert
  expect(storeGet('network' as any)).toEqual('mainnet');
    });
  });
});
