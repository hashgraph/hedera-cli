const path = require("path");
const dotenv = require("dotenv");

const { saveConfig } = require("../utils/configManager");

module.exports = (program) => {
  program
    .command("setup")
    .description("Setup the CLI with operator key and ID")
    .action(() => {
      setup();
    });
};

function setup() {
  // Path to the .env file in the .hedera directory in the user's home directory
  const envPath = path.join(process.env.HOME, ".hedera/.env");

  // Load environment variables from .env file
  const envConfig = dotenv.config({ path: envPath });

  // Check for errors in loading .env file
  if (envConfig.error) {
    console.error("Error loading .env file:", envConfig.error.message);
    return;
  }

  // Extract operator key and ID from environment variables
  const { OPERATOR_KEY, OPERATOR_ID } = process.env;

  // Validate operator key and ID
  if (!OPERATOR_KEY || !OPERATOR_ID) {
    console.error(
      "OPERATOR_KEY and OPERATOR_ID must be defined in the .env file"
    );
    return;
  }

  // Create/update config.json with operator key and ID
  const config = {
    operatorKey: OPERATOR_KEY,
    operatorId: OPERATOR_ID,
    network: "testnet",
    mirrorNodeTestnet: "https://testnet.mirrornode.hedera.com/api/v1",
    mirrorNodeMainnet: "https://mainnet.mirrornode.hedera.com/api/v1",
    recording: 0,
    recordingScriptName: "",
    accounts: {},
    scripts: {},
  };

  saveConfig(config);
}
