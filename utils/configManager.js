const fs = require("fs");
const path = require("path");
const {
  Client,
  AccountId,
  PrivateKey,
  NetworkName,
} = require("@hashgraph/sdk");
const configPath = path.join(__dirname, "../config.json");

let config = loadConfig();

function loadConfig() {
  try {
    const configFile = fs.readFileSync(configPath, "utf-8");
    return JSON.parse(configFile);
  } catch (error) {
    console.error("Error loading config:", error.message);
    return {};
  }
}

function saveConfig(newConfig) {
  try {
    fs.writeFileSync(configPath, JSON.stringify(newConfig, null, 2), "utf-8");
    config = newConfig; // Update in-memory config
    console.log("Config saved.");
  } catch (error) {
    console.error("Error saving config:", error.message);
  }
}

function getMirrorNodeURL() {
  const mirrorNodeURL = config.network === "testnet" ? config.mirrorNodeTestnet : config.mirrorNodeMainnet;
  return mirrorNodeURL;
}

function getHederaClient() {
  const client = createHederaClient(
    config.network,
    config.operatorId,
    config.operatorKey
  );

  return client;
}

function getConfig() {
  return config;
}

function createHederaClient(network, operatorId, operatorKey) {
  let hederaNetwork;

  switch (network) {
    case "mainnet":
      hederaNetwork = NetworkName.Mainnet;
      break;
    case "testnet":
      hederaNetwork = NetworkName.Testnet;
      break;
    default:
      throw new Error(`Unsupported network: ${network}`);
  }

  return Client.forNetwork(hederaNetwork).setOperator(
    AccountId.fromString(operatorId),
    PrivateKey.fromString(operatorKey)
  );
}

/** hook (middleware)
 * @example command ['account', 'create', '-b', '1000', '-t', 'ed25519'] 
 */
function recordCommand(command) {
  if (config.recording === 1) {
    config.scripts[config.recordingScriptName].commands.push(command.join(" "));
    saveConfig(config);
  }
}

module.exports = {
  loadConfig,
  saveConfig,
  getConfig,

  getMirrorNodeURL,
  getHederaClient,

  recordCommand,
};
