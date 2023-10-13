const path = require("path");
const dotenv = require("dotenv");

const { saveState } = require("../state/stateController");
const config = require("../state/config.json");

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

  // Update state.json with operator key and ID
  const setupState = {
    ...config,
  };
  setupState.operatorKey = OPERATOR_KEY;
  setupState.operatorId = OPERATOR_ID;
  saveState(setupState);
}
