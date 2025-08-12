// Jest setup file to silence logger console output for tests that do not assert on logs.
// Tests that need to see actual console output can unset the env var locally or adjust logger level.
process.env.HCLI_SUPPRESS_CONSOLE = '1';
