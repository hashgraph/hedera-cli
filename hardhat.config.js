require('dotenv').config();
require('@nomicfoundation/hardhat-toolbox');
require('@nomicfoundation/hardhat-ethers');
require('hardhat-deploy')
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
      accounts: ["<your-hex-private-key>"],
      chainId: 295,
    },*/
    testnet: {
      url: stateController.default.get('rpcUrlTestnet'),
      accounts: ["0x9d3d4a4012e84359a4d117b8558696c2d72b0d61118f977394adeb6274de8c59"],
      chainId: 296,
    },
    /*previewnet: {
      url: stateController.default.get('rpcUrlPreviewnet'),
      accounts: ["<your-hex-private-key>"],
      chainId: 297,
    },*/
    local: {
      url: 'http://localhost:7546',
      accounts: [
        '0x105d050185ccb907fba04dd92d8de9e32c18305e097ab41dadda21489a211524'
      ],
      chainId: 298,
    },
  },
};
