export default class BadEntityIdError extends Error {
    /**
     * @param {Long} shard
     * @param {Long} realm
     * @param {Long} num
     * @param {string} presentChecksum
     * @param {string} expectedChecksum
     */
    constructor(shard: Long, realm: Long, num: Long, presentChecksum: string, expectedChecksum: string);
    shard: import("long").Long;
    realm: import("long").Long;
    num: import("long").Long;
    presentChecksum: string;
    expectedChecksum: string;
}
