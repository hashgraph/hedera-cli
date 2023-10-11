/**
 * @typedef {import("./Status.js").default} Status
 * @typedef {import("./transaction/TransactionId.js").default} TransactionId
 * @typedef {import("./transaction/TransactionReceipt.js").default} TransactionReceipt
 */
export default class ReceiptStatusError extends StatusError {
    /**
     * @param {object} props
     * @param {TransactionReceipt} props.transactionReceipt
     * @param {Status} props.status
     * @param {TransactionId} props.transactionId
     */
    constructor(props: {
        transactionReceipt: TransactionReceipt;
        status: Status;
        transactionId: TransactionId;
    });
    /**
     * @type {TransactionReceipt}
     * @readonly
     */
    readonly transactionReceipt: TransactionReceipt;
}
export type Status = import("./Status.js").default;
export type TransactionId = import("./transaction/TransactionId.js").default;
export type TransactionReceipt = import("./transaction/TransactionReceipt.js").default;
import StatusError from "./StatusError.js";
