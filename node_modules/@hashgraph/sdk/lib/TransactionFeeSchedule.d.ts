export default class TransactionFeeSchedule {
    /**
     * @param {Uint8Array} bytes
     * @returns {TransactionFeeSchedule}
     */
    static fromBytes(bytes: Uint8Array): TransactionFeeSchedule;
    /**
     * @internal
     * @param {HashgraphProto.proto.ITransactionFeeSchedule} transactionFeeSchedule
     * @returns {TransactionFeeSchedule}
     */
    static _fromProtobuf(transactionFeeSchedule: HashgraphProto.proto.ITransactionFeeSchedule): TransactionFeeSchedule;
    /**
     * @param {object} [props]
     * @param {RequestType} [props.hederaFunctionality]
     * @param {FeeData} [props.feeData]
     * @param {FeeData[]} [props.fees]
     */
    constructor(props?: {
        hederaFunctionality?: RequestType | undefined;
        feeData?: FeeData | undefined;
        fees?: FeeData[] | undefined;
    } | undefined);
    hederaFunctionality: RequestType | undefined;
    feeData: FeeData | undefined;
    fees: FeeData[] | undefined;
    /**
     * @internal
     * @returns {HashgraphProto.proto.ITransactionFeeSchedule}
     */
    _toProtobuf(): HashgraphProto.proto.ITransactionFeeSchedule;
    /**
     * @returns {Uint8Array}
     */
    toBytes(): Uint8Array;
}
import RequestType from "./RequestType.js";
import FeeData from "./FeeData.js";
import * as HashgraphProto from "@hashgraph/proto";
