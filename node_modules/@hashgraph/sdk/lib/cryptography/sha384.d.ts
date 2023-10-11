/**
 * @param {Uint8Array} data
 * @returns {Promise<Uint8Array>}
 */
export function digest(data: Uint8Array): Promise<Uint8Array>;
/**
 * @param {Uint8Array} data
 * @returns {Uint8Array}
 */
export function digestSync(data: Uint8Array): Uint8Array;
