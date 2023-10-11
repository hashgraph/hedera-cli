const fs = require('fs');
const path = require('path');

const { recordCommand } = require("../utils/configManager");
const switchNetworkUtils = require("../utils/switchNetwork");

module.exports = (program) => {
  const network = program.command("backup");

  network
    .command("create")
    .hook("preAction", (thisCommand) => {
      recordCommand(thisCommand.parent.args);
    })
    .description("Create a backup of the config.json file")
    .action(() => {
      backupConfig();
    });
};


function backupConfig() {
  // 1. Generate a Timestamp
  const timestamp = Date.now(); // UNIX timestamp in milliseconds
  
  // 2. Create backup filename
  const backupFilename = `config.backup.${timestamp}.json`;
  const configPath = path.join(__dirname, '..', 'config.json');
  const backupPath = path.join(__dirname, '..', backupFilename);
  
  // 3. Read original config
  fs.readFile(configPath, 'utf8', (readErr, data) => {
    if (readErr) {
      console.error('Error reading the config file:', readErr);
      return;
    }
    
    // 4. Write Backup File
    fs.writeFile(backupPath, data, 'utf8', (writeErr) => {
      // 5. Handle Errors
      if (writeErr) {
        console.error('Error creating the backup file:', writeErr);
        return;
      }
      
      // 6. Confirmation
      console.log(`Backup created successfully: ${backupFilename}`);
    });
  });
}
