/**
 * @typedef {import("./Status.js").default} Status
 * @typedef {import("./transaction/TransactionId.js").default} TransactionId
 * @typedef {import("./transaction/TransactionRecord").default} TransactionRecord
 */
export default class RecordStatusError extends StatusError {
    /**
     * @param {object} props
     * @param {TransactionRecord} props.transactionRecord
     * @param {Status} props.status
     * @param {TransactionId} props.transactionId
     */
    constructor(props: {
        transactionRecord: TransactionRecord;
        status: Status;
        transactionId: TransactionId;
    });
    /**
     * @type {TransactionRecord}
     * @readonly
     */
    readonly transactionRecord: TransactionRecord;
}
export type Status = import("./Status.js").default;
export type TransactionId = import("./transaction/TransactionId.js").default;
export type TransactionRecord = import("./transaction/TransactionRecord").default;
import StatusError from "./StatusError.js";
