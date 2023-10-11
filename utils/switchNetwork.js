const fs = require("fs");
const path = require("path");

const configPath = path.join(__dirname, "../config.json");

function switchNetwork(name) {
  if (!["mainnet", "testnet"].includes(name)) {
    console.error("Invalid network name. Available networks: mainnet, testnet");
    return;
  }

  // Load existing config
  let config;
  try {
    config = JSON.parse(fs.readFileSync(configPath, "utf-8"));
  } catch (error) {
    console.error("Error reading config file:", error.message);
    return;
  }

  // Update the network in the config
  config.network = name;

  // Save the updated config back to the file
  try {
    fs.writeFileSync(configPath, JSON.stringify(config, null, 2), "utf-8");
    console.log(`Switched to ${name}`);
  } catch (error) {
    console.error("Error writing to config file:", error.message);
  }
}

module.exports = {
    switchNetwork
};
