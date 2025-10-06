require('dotenv').config();
require('@nomicfoundation/hardhat-toolbox');
require('@nomicfoundation/hardhat-ethers');
require('hardhat-deploy');
const stateController = require('./dist/state/stateController');

module.exports = {
  solidity: {
    version: '0.8.22',
    settings: {
      optimizer: {
        enabled: true,
        runs: 500,
      },
    },
  },
  paths: {
    sources: './dist/contracts',
    tests: './dist/test',
    cache: './dist/cache',
    artifacts: './dist/artifacts',
  },
  defaultNetwork: 'local',
  networks: {
    /*mainnet: {
      url: stateController.default.get('rpcUrlMainnet'),
      accounts: [process.env.MAINNET_OPERATOR_KEY_HEX],
      chainId: 295,
    },*/
    /*testnet: {
      url: stateController.default.get('rpcUrlTestnet'),
      accounts: [process.env.TESTNET_OPERATOR_KEY_HEX],
      chainId: 296,
    },*/
    /*previewnet: {
      url: stateController.default.get('rpcUrlPreviewnet'),
      accounts: [process.env.PREVIEWNET_OPERATOR_KEY_HEX],
      chainId: 297,
    },*/
    local: {
      url: 'http://localhost:7546',
      accounts: [
        '0x105d050185ccb907fba04dd92d8de9e32c18305e097ab41dadda21489a211524',
      ],
      chainId: 298,
    },
  },
};
