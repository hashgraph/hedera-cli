// https://testnet.mirrornode.hedera.com/api/v1/topics/0.0.7755571/messages
// note that messages are converted to base64

import { baseState, fullState, bob, alice } from "../../helpers/state";
import { Command } from "commander";
import commands from "../../../src/commands";
import stateController from "../../../src/state/stateController";
import hbarUtils from "../../../src/utils/hbar";

jest.mock("../../src/state/state"); // Mock the original module -> looks for __mocks__/state.ts in same directory

describe("topic message command", () => {
  // const hbarUtilsSpy = jest.spyOn(hbarUtils, 'transfer').mockResolvedValue()

  beforeEach(() => {
    stateController.saveState(baseState);
  });

  describe("topic message submit - success path", () => {
    afterEach(() => {
      // Spy cleanup
      // hbarUtilsSpy.mockClear();
    });

    test("✅ Submit message to topic", async () => {
        // Assert
        expect(1).toEqual(1);
    });
  });

  describe("topic message find - success path", () => {
    afterEach(() => {
      // Spy cleanup
      // hbarUtilsSpy.mockClear();
    });

    test("✅ Find message by sequence number for topic", async () => {
        // Assert
        expect(1).toEqual(1);
    });
  });
});
