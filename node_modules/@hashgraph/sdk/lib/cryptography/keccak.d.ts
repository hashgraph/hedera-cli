/**
 * @type {(message: string) => string}
 */
export const keccak256: (message: string) => string;
export type KeccakT = {
    blocks: number[];
    blockCount: number;
    outputBlocks: number;
    s: number[];
    start: number;
    block: number;
    reset: boolean;
    lastByteIndex?: number | undefined;
};
