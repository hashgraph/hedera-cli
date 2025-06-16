import stateUtils from '../../utils/state';
import { Logger } from '../../utils/logger';
import telemetryUtils from '../../utils/telemetry';
const hre = require('hardhat');

import type { Command } from '../../../types';

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command('deploy')
    .hook('preAction', async (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      if (stateUtils.isTelemetryEnabled()) {
        await telemetryUtils.recordCommand(command.join(' '));
      }
    })
    .description('Deploy smart contracts')
    .action(async () => {
      logger.verbose('Deploying smart contracts');

      try {
        console.log('Loading Hardhat environment...');
        const [deployer] = await hre.ethers.getSigners();
        console.log('Deploying contracts with:', deployer.address);

        // compile & get factory
        const ERC20 = await hre.ethers.getContractFactory('MyToken');
        logger.log('Contracts deployed successfully.');

        const contract = await ERC20.deploy(deployer.address);
        await contract.deployed();
        console.log('Contract deployed at:', contract.target);
      } catch (error) {
        logger.error('Failed to deploy contracts:', error as object);
      }
    });
};
