/**
 * @typedef {object} MirrorError
 * @property {number} code
 * @property {string} details
 */
/**
 * @internal
 * @abstract
 */
export default class MirrorChannel {
    /**
     * @abstract
     * @returns {void}
     */
    close(): void;
    /**
     * @abstract
     * @internal
     * @param {string} serviceName
     * @param {string} methodName
     * @param {Uint8Array} requestData
     * @param {(data: Uint8Array) => void} callback
     * @param {(error: MirrorError | Error) => void} error
     * @param {() => void} end
     * @returns {() => void}
     */
    makeServerStreamRequest(serviceName: string, methodName: string, requestData: Uint8Array, callback: (data: Uint8Array) => void, error: (error: MirrorError | Error) => void, end: () => void): () => void;
}
export type MirrorError = {
    code: number;
    details: string;
};
