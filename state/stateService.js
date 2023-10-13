const {
  Client,
  AccountId,
  PrivateKey,
  NetworkName,
} = require("@hashgraph/sdk");
const {
  getState,
  getAllState,
  saveState,
  saveStateAttribute,
} = require("./stateController");

/** hook (middleware)
 * @example command ['account', 'create', '-b', '1000', '-t', 'ed25519']
 */
function recordCommand(command) {
  const state = getAllState();
  if (state.recording === 1) {
    state.scripts[state.recordingScriptName].commands.push(command.join(" "));

    saveState(state);
  }
}

function getMirrorNodeURL() {
  const network = getState("network");
  const mirrorNodeURL =
    network === "testnet"
      ? getState("mirrorNodeTestnet")
      : getState("mirrorNodeMainnet");
  return mirrorNodeURL;
}

function getHederaClient() {
  const state = getAllState();
  let hederaNetwork;

  switch (state.network) {
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
    AccountId.fromString(state.operatorId),
    PrivateKey.fromString(state.operatorKey)
  );
}

function switchNetwork(name) {
  if (!["mainnet", "testnet"].includes(name)) {
    console.error("Invalid network name. Available networks: mainnet, testnet");
    return;
  }

  saveStateAttribute("network", name);
}

module.exports = {
  getMirrorNodeURL,
  getHederaClient,
  recordCommand,
  switchNetwork,
};
