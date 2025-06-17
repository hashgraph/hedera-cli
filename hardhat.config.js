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
    /*testnet: {
      url: stateController.default.get('rpcUrlTestnet'),
      accounts: ["<your-hex-private-key>"],
      chainId: 296,
    },*/
    /*previewnet: {
      url: stateController.default.get('rpcUrlPreviewnet'),
      accounts: ["<your-hex-private-key>"],
      chainId: 297,
    },*/
    local: {
      url: 'http://localhost:7546',
      accounts: [
        '0x105d050185ccb907fba04dd92d8de9e32c18305e097ab41dadda21489a211524',
        '0x2e1d968b041d84dd120a5860cee60cd83f9374ef527ca86996317ada3d0d03e7'
      ],
      chainId: 298,
    },
  },
};
