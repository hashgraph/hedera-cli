import { z } from 'zod';
import type { NetworkConfig, State } from '../../types';

// Network config schema allowing partial overrides
export const networkConfigSchema: z.ZodType<Partial<NetworkConfig>> = z
  .object({
    mirrorNodeUrl: z.string().url().optional(),
    rpcUrl: z.string().url().optional(),
    operatorKey: z.string().optional(),
    operatorId: z.string().optional(),
    hexKey: z.string().optional(),
  })
  .strict();

// User config overlay schema (partial State – only allow override-able keys)
export const userConfigSchema = z
  .object({
    network: z.string().optional(),
    telemetry: z.number().int().min(0).max(1).optional(),
    telemetryServer: z.string().url().optional(),
    networks: z.record(z.string(), networkConfigSchema).optional(),
  })
  .strict();

export type UserConfigOverlay = z.infer<typeof userConfigSchema>;

export interface ValidationResult {
  valid: boolean;
  errors?: string[];
  config: UserConfigOverlay;
}

export function validateUserConfig(raw: unknown): ValidationResult {
  const result = userConfigSchema.safeParse(raw);
  if (result.success) {
    return { valid: true, config: result.data };
  }
  return {
    valid: false,
    config: {},
    errors: result.error.issues.map(
      (i: { path: (string | number)[]; message: string }) =>
        `${i.path.join('.') || '<root>'}: ${i.message}`,
    ),
  };
}

// Helper to merge validated overlay into base config (networks deep merge)
export function mergeUserConfig<T extends State>(
  base: T,
  overlay: UserConfigOverlay,
): T {
  const merged: T = { ...base };
  if (overlay.network) merged.network = overlay.network;
  if (typeof overlay.telemetry === 'number')
    merged.telemetry = overlay.telemetry;
  if (overlay.telemetryServer) merged.telemetryServer = overlay.telemetryServer;
  if (overlay.networks) {
    merged.networks = { ...merged.networks };
    for (const [k, v] of Object.entries(overlay.networks)) {
      const existing = merged.networks[k] || ({} as NetworkConfig);
      merged.networks[k] = { ...existing, ...v } as NetworkConfig;
    }
  }
  return merged;
}
