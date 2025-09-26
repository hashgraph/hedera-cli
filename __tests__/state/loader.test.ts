import * as fs from 'fs';
import * as path from 'path';

// Ensure fresh module load per test to pick up env changes
const fresh = <T>(mod: string): T => {
  // eslint-disable-next-line @typescript-eslint/no-var-requires
  delete require.cache[require.resolve(mod)];
  // eslint-disable-next-line @typescript-eslint/no-var-requires
  return require(mod);
};

describe('config loader', () => {
  const originalCwd = process.cwd();
  const tmpDir = fs.mkdtempSync(path.join(originalCwd, 'loader-test-'));
  afterEach(() => {
    try {
      process.chdir(originalCwd);
    } catch {
      /* ignore */
    }
  });
  afterAll(() => {
    try {
      process.chdir(originalCwd);
    } catch {
      /* ignore */
    }
    try {
      fs.rmSync(tmpDir, { recursive: true, force: true });
    } catch {
      /* ignore */
    }
  });

  test('HCLI_CONFIG_FILE explicit path overrides cosmiconfig search', () => {
    const fileA = path.join(tmpDir, 'a.json');
    const fileB = path.join(tmpDir, '.hedera-clirc.json');
    fs.writeFileSync(fileA, JSON.stringify({ network: 'mainnet' }), 'utf-8');
    fs.writeFileSync(fileB, JSON.stringify({ network: 'previewnet' }), 'utf-8');

    process.env.HCLI_CONFIG_FILE = fileA;
    process.chdir(tmpDir);

    const { loadUserConfig } = fresh<{ loadUserConfig: any }>(
      '../../src/config/loader',
    );
    const { user, source } = loadUserConfig();

    expect(user.network).toBe('mainnet');
    expect(source).toBe(fileA);

    delete process.env.HCLI_CONFIG_FILE;
  });

  test('cosmiconfig discovery when no explicit file', () => {
    const fileB = path.join(tmpDir, '.hedera-clirc.json');
    fs.writeFileSync(fileB, JSON.stringify({ network: 'previewnet' }), 'utf-8');
    process.chdir(tmpDir); // allow cosmiconfig to discover
    const { loadUserConfig } = fresh<{ loadUserConfig: any }>(
      '../../src/config/loader',
    );
    const { user } = loadUserConfig();
    expect(user.network).toBe('previewnet');
  });

  test('resolveStateFilePath respects HCLI_STATE_FILE', () => {
    const custom = path.join(tmpDir, 'custom-state.json');
    process.env.HCLI_STATE_FILE = custom;
    const { resolveStateFilePath } = fresh<{ resolveStateFilePath: any }>(
      '../../src/config/loader',
    );
    expect(resolveStateFilePath()).toBe(custom);
    delete process.env.HCLI_STATE_FILE;
  });
});
