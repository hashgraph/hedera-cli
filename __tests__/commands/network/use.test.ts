import { baseState } from '../../helpers/state';
import { Command } from 'commander';
import commands from '../../../src/commands';
import stateController from '../../../src/state/stateController';

jest.mock('../../../src/state/state'); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe('network use command', () => {
  const stateControllerSpy = jest.spyOn(stateController, 'saveKey');

  beforeEach(() => {
    const stateCopy = {
      ...baseState,
      // Provide a bogus mainnet operator ID and key
      mainnetOperatorId: '0.0.1001',
      mainnetOperatorKey:
        '302e020100300506032b65700422042091132178e72057a1d7528025956fe39b0b847f200ab59b2fdd367017f3087137',
    };

    stateController.saveState(stateCopy);
  });

  describe('network use - success path', () => {
    afterEach(() => {
      // Spy cleanup
      stateControllerSpy.mockClear();
    });

    test('✅ switch to mainnet', async () => {
      // Assert
      expect(stateController.get('network')).toEqual('localnet');

      // Arrange
      const program = new Command();
      commands.networkCommands(program);

      // Act
      program.parse(['node', 'hedera-cli.ts', 'network', 'use', 'mainnet']);

      // Assert
      expect(stateControllerSpy).toHaveBeenCalledWith('network', 'mainnet');
    });
  });
});
