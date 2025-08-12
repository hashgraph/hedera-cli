// Jest setup file to silence logger console output for tests that do not assert on logs.
// Tests that need to see actual console output can change HCLI_LOG_MODE to normal or verbose before logger import.
process.env.HCLI_LOG_MODE = 'silent';
