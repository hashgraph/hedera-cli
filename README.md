.env stored in `.hedera/.env` file under the root of the user's home directory (`process.env.HOME`)

Unit testing
you want to create a local clone of commander program each time you run a unit test to ensure test encapsulation: `const program = new Command();`


const { Command } = require('commander');
const networkCommands = require("../../commands/network");

const fs = require("fs");

describe("network commands", () => {

  describe("network switch command", () => {
    test("switching networks successfully", () => {
      // Arrange
      fs.readFileSync = jest.fn(() => JSON.stringify({ network: "mainnet" })); // Mock fs.readFileSync to return a sample config
      fs.writeFileSync = jest.fn(); // Mock fs.writeFileSync to do nothing
      //console.log = jest.fn(); // Mock console.log to check the log messages

      const program = new Command();
      networkCommands(program);

      // Act
      program.parse(["node", "hedera-cli.js", "network", "use", "testnet"]);

      // Assert
      const opts = program.opts();
      console.log(opts)
      expect(opts.network).toBe("testnet");
      console.log(program.args);
      // expect(program.args).toEqual(["--type", "order-cake"]);

      // Check that console.log was called with the correct message
      expect(console.log).toHaveBeenCalledWith("Switched to testnet");

      // Check that fs.writeFileSync was called with the updated config
      expect(fs.writeFileSync).toHaveBeenCalledWith(
        expect.any(String), // path
        JSON.stringify({ network: "testnet" }, null, 2),
        "utf-8"
      );
    });
  });

  describe("network switch ls", () => {
    // TODO
  });
});

# Circle-CI
https://circleci.com/blog/testing-command-line-applications/ 

# Notes for dev
when updating the config, make sure to update the setup object in the setup.js file for config.json

# Exporting/Importing keys:
See config.json to export

To import, only type, account ID, and priv key needed. 

Interface in config.json:
"accounts": {
    "michiel": {
      "accountId": "0.0.4521326",
      "type": "ECDSA",
      "publicKey": "302d300706052b8104000a03220003042679fbb4555e198f49d52fac8fd7f8a15baa0a146c491cf205d32b8ab1f802",
      "evmAddress": "17523ad226694146542f438eae14fe5198343d18",
      "solidityAddress": "000000000000000000000000000000000044fd6e",
      "solidityAddressFull": "0x000000000000000000000000000000000044fd6e",
      "privateKey": "3030020100300706052b8104000a04220420fad0e2871c85ce738ad86c016ee75356aa3eb3c00e3f8cd4d626bbf8368f3aec"
    }
}

## Account create
If you provide "random" as an alias it will generate a random name.

## Account reset addressbook
Useful for CI pipeline where you don’t want to run into alias already exists issues 