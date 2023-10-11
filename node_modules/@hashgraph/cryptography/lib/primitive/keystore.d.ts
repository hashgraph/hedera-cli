/**
 * @typedef {object} KeystoreKdfParams
 * @property {number} dkLen
 * @property {string} salt
 * @property {number} c
 * @property {string} prf
 */
/**
 * @typedef {object} KeystoreCipherParams
 * @property {string} iv
 */
/**
 * @typedef {object} KeystoreCrypto
 * @property {string} ciphertext
 * @property {KeystoreCipherParams} cipherparams
 * @property {string} cipher
 * @property {string} kdf
 * @property {KeystoreKdfParams} kdfparams
 * @property {string} mac
 */
/**
 * @typedef {object} Keystore
 * @property {number} version
 * @property {KeystoreCrypto} crypto
 */
/**
 * @param {Uint8Array} privateKey
 * @param {string} passphrase
 * @returns {Promise<Uint8Array>}
 */
export function createKeystore(privateKey: Uint8Array, passphrase: string): Promise<Uint8Array>;
/**
 * @param {Uint8Array} keystoreBytes
 * @param {string} passphrase
 * @returns {Promise<Uint8Array>}
 */
export function loadKeystore(keystoreBytes: Uint8Array, passphrase: string): Promise<Uint8Array>;
export type KeystoreKdfParams = {
    dkLen: number;
    salt: string;
    c: number;
    prf: string;
};
export type KeystoreCipherParams = {
    iv: string;
};
export type KeystoreCrypto = {
    ciphertext: string;
    cipherparams: KeystoreCipherParams;
    cipher: string;
    kdf: string;
    kdfparams: KeystoreKdfParams;
    mac: string;
};
export type Keystore = {
    version: number;
    crypto: KeystoreCrypto;
};
