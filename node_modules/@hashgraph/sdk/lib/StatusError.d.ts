/**
 * @typedef {import("./Status.js").default} Status
 * @typedef {import("./transaction/TransactionId.js").default} TransactionId
 */
/**
 * @typedef {object} StatusErrorJSON
 * @property {string} name
 * @property {string} status
 * @property {string} transactionId
 * @property {string} message
 */
export default class StatusError extends Error {
    /**
     * @param {object} props
     * @param {Status} props.status
     * @param {TransactionId} props.transactionId
     * @param {string} message
     */
    constructor(props: {
        status: Status;
        transactionId: TransactionId;
    }, message: string);
    status: import("./Status.js").default;
    transactionId: import("./transaction/TransactionId.js").default;
    /**
     * @returns {StatusErrorJSON}
     */
    toJSON(): StatusErrorJSON;
    /**
     * @returns {StatusErrorJSON}
     */
    valueOf(): StatusErrorJSON;
}
export type Status = import("./Status.js").default;
export type TransactionId = import("./transaction/TransactionId.js").default;
export type StatusErrorJSON = {
    name: string;
    status: string;
    transactionId: string;
    message: string;
};
