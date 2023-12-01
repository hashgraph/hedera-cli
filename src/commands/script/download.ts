const axios = require("axios");

import { recordCommand } from "../../state/stateService";
import { Logger } from "../../utils/logger";
import stateController from "../../state/stateController";

import type { Command, Script } from "../../../types";

const logger = Logger.getInstance();

export default (program: any) => {
  program
    .command("download")
    .hook("preAction", (thisCommand: Command) => {
      const command = [
        thisCommand.parent.action().name(),
        ...thisCommand.parent.args,
      ];
      recordCommand(command);
    })
    .description("Download a script from a URL")
    .requiredOption("-u, --url <url>", "URL of script to download")
    .action(async (options: DownloadScriptOptions) => {
      downloadScript(options.url);
    });
};

async function downloadScript(url: string) {
  let data;
  try {
    const response = await axios.get(url);
    data = response.data;
  } catch (error) {
    console.error("Error downloading the file:", error);
    logger.error(error as object);
  }

  const scripts: Record<string, Script> = stateController.get("scripts");
  data.scripts.forEach((script: Script) => {
    const scriptName = `script-${script.name}`;
    const existingScript = scripts[scriptName];

    if (existingScript) {
      console.error(`Script with name ${scriptName} already exists`);
      return;
    }

    scripts[scriptName] = {
      name: script.name,
      creation: Date.now(),
      commands: script.commands,
    };
    stateController.saveKey("scripts", scripts);
    console.log(`Script "${script.name}" added successfully`);
  });
}

interface DownloadScriptOptions {
  url: string;
}
