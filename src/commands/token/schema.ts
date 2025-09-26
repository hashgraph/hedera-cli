import { z } from 'zod';
import type { CustomFeeInput, Keys } from '../../../types/state';

// Loose Hedera account id pattern (shard.realm.num)
const accountIdRegex = /^\d+\.\d+\.\d+$/;

export const keysSchema = z
  .object({
    adminKey: z.string(),
    supplyKey: z.string(),
    wipeKey: z.string(),
    kycKey: z.string(),
    freezeKey: z.string(),
    pauseKey: z.string(),
    feeScheduleKey: z.string(),
    treasuryKey: z
      .string()
      .min(1, 'treasuryKey is required (can reference <name:...>)'),
  })
  .strict();

const fixedFeeSchema = z
  .object({
    type: z.literal('fixed'),
    amount: z.number().int().positive(),
    unitType: z.string(),
    denom: z.string().optional(),
    collectorId: z
      .string()
      .regex(accountIdRegex, 'collectorId must be a valid account id')
      .optional(),
    exempt: z.boolean().optional(),
  })
  .strict()
  .superRefine((fee, ctx) => {
    const unit = fee.unitType.toLowerCase();
    const allowed = ['hbar', 'hbars', 'tinybar', 'tinybars', 'token', 'tokens'];
    if (!allowed.includes(unit)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: `Invalid unitType: ${fee.unitType}`,
      });
    }
    if (['token', 'tokens'].includes(unit) && !fee.denom) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: 'denom required for token unitType',
      });
    }
  });

const fractionalFeeSchema = z
  .object({
    type: z.literal('fractional'),
    numerator: z.number().int().positive(),
    denominator: z.number().int().positive(),
    min: z.number().int().nonnegative().optional(),
    max: z.number().int().nonnegative().optional(),
    collectorId: z
      .string()
      .regex(accountIdRegex, 'collectorId must be a valid account id')
      .optional(),
    exempt: z.boolean().optional(),
  })
  .strict()
  .superRefine((fee, ctx) => {
    if (fee.denominator === 0) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: 'denominator cannot be zero',
      });
    }
    if (fee.min != null && fee.max != null && fee.min > fee.max) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: 'min cannot be greater than max',
      });
    }
  });

export const customFeeSchema = z.union([fixedFeeSchema, fractionalFeeSchema]);

export const tokenFileSchema = z
  .object({
    name: z.string().min(1).max(100),
    symbol: z.string().min(1).max(20),
    decimals: z.number().int().min(0).max(18),
    supplyType: z.union([z.literal('finite'), z.literal('infinite')]),
    initialSupply: z.number().int().nonnegative(),
    maxSupply: z.number().int().nonnegative().default(0),
    keys: keysSchema,
    customFees: z.array(customFeeSchema).default([]),
    memo: z.string().max(100).optional().default(''),
  })
  .strict()
  .superRefine((val, ctx) => {
    if (val.supplyType === 'finite' && (!val.maxSupply || val.maxSupply <= 0)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: 'maxSupply must be > 0 for finite supplyType',
      });
    }
    if (val.supplyType === 'finite' && val.initialSupply > val.maxSupply) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: 'initialSupply cannot exceed maxSupply',
      });
    }
  });

export type TokenFileDefinition = z.infer<typeof tokenFileSchema>;

export interface TokenValidationResult {
  valid: boolean;
  errors?: string[];
  data?: TokenFileDefinition;
}

export function validateTokenFile(raw: unknown): TokenValidationResult {
  const parsed = tokenFileSchema.safeParse(raw);
  if (parsed.success) return { valid: true, data: parsed.data };
  return {
    valid: false,
    errors: parsed.error.issues.map(
      (i) => `${i.path.join('.') || '<root>'}: ${i.message}`,
    ),
  };
}

export function mapToTokenInput(def: TokenFileDefinition) {
  return {
    name: def.name,
    symbol: def.symbol,
    decimals: def.decimals,
    supplyType: def.supplyType,
    initialSupply: def.initialSupply,
    maxSupply: def.maxSupply,
    keys: def.keys as Keys,
    customFees: def.customFees as CustomFeeInput[],
    memo: def.memo,
    treasuryKey: def.keys.treasuryKey,
  };
}
