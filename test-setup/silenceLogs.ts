// Central Jest setup file to silence logger output by default.
// Tests that need logs can set process.env.HCLI_LOG_MODE before importing logger.
process.env.HCLI_LOG_MODE = 'silent';
