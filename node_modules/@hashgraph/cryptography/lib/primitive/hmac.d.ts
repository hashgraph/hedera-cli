/**
 * @param {HashAlgorithm} algorithm
 * @param {Uint8Array | string} secretKey
 * @param {Uint8Array | string} data
 * @returns {Promise<Uint8Array>}
 */
export function hash(algorithm: HashAlgorithm, secretKey: Uint8Array | string, data: Uint8Array | string): Promise<Uint8Array>;
export type HashAlgorithm = string;
export namespace HashAlgorithm {
    const Sha256: string;
    const Sha384: string;
    const Sha512: string;
}
