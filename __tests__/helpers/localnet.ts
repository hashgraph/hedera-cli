import api from '../../src/api';
import { waitFor } from './poll';

export async function initLocalnetFlag(): Promise<void> {
  if (globalThis.__LOCALNET_AVAILABLE__ !== undefined) return;
  const ok = await waitFor(
    async () => {
      try {
        const res = await api.account.getAccountInfo('0.0.2');
        return !!res?.data;
      } catch {
        return false;
      }
    },
    {
      timeout: 5000,
      interval: 500,
      description: 'localnet availability probe',
    },
  ).catch(() => false);
  globalThis.__LOCALNET_AVAILABLE__ = ok;
  if (!ok) {
    // eslint-disable-next-line no-console
    console.warn('[e2e] Localnet unavailable: tests will be soft-skipped.');
  }
}

export function localnetTest(
  name: string,
  fn: () => unknown | Promise<unknown>,
) {
  test(name, async () => {
    if (globalThis.__LOCALNET_AVAILABLE__ === false) return; // soft-skip
    return fn();
  });
}
