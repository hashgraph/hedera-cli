import * as fs from "fs";
import * as path from "path";

import { recordCommand } from "../state/stateService";

import type { Command } from "../../types";

export default (program: any) => {
    const network = program.command("backup");

  network
    .command("create")
    .hook("preAction", (thisCommand: Command) => {
      recordCommand(thisCommand.parent.args);
    })
    .description("Create a backup of the config.json file")
    .action(() => {
      backupState();
    });
};

function backupState() {
  const timestamp = Date.now(); // UNIX timestamp in milliseconds

  // Create backup filename
  const backupFilename = `state.backup.${timestamp}.json`;
  const statePath = path.join(__dirname, "..", "state", "state.json");
  const backupPath = path.join(__dirname, "..", "state", backupFilename);

  let data;
  try {
    data = fs.readFileSync(statePath, "utf8");
  } catch (error) {
    console.error("Error reading the state file:", error);
    return;
  }

  try {
    fs.writeFileSync(backupPath, data, "utf8");
  } catch (error) {
    console.error("Error creating the backup file:", error);
    return;
  }

  console.log(`Backup created successfully: ${backupFilename}`);
}
