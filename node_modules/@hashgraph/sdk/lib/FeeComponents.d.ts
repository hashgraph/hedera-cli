export default class FeeComponents {
    /**
     * @param {Uint8Array} bytes
     * @returns {FeeComponents}
     */
    static fromBytes(bytes: Uint8Array): FeeComponents;
    /**
     * @internal
     * @param {HashgraphProto.proto.IFeeComponents} feeComponents
     * @returns {FeeComponents}
     */
    static _fromProtobuf(feeComponents: HashgraphProto.proto.IFeeComponents): FeeComponents;
    /**
     * @param {object} [props]
     * @param {Long} [props.min]
     * @param {Long} [props.max]
     * @param {Long} [props.constant]
     * @param {Long} [props.transactionBandwidthByte]
     * @param {Long} [props.transactionVerification]
     * @param {Long} [props.transactionRamByteHour]
     * @param {Long} [props.transactionStorageByteHour]
     * @param {Long} [props.contractTransactionGas]
     * @param {Long} [props.transferVolumeHbar]
     * @param {Long} [props.responseMemoryByte]
     * @param {Long} [props.responseDiskByte]
     */
    constructor(props?: {
        min?: import("long").Long | undefined;
        max?: import("long").Long | undefined;
        constant?: import("long").Long | undefined;
        transactionBandwidthByte?: import("long").Long | undefined;
        transactionVerification?: import("long").Long | undefined;
        transactionRamByteHour?: import("long").Long | undefined;
        transactionStorageByteHour?: import("long").Long | undefined;
        contractTransactionGas?: import("long").Long | undefined;
        transferVolumeHbar?: import("long").Long | undefined;
        responseMemoryByte?: import("long").Long | undefined;
        responseDiskByte?: import("long").Long | undefined;
    } | undefined);
    min: import("long").Long | undefined;
    max: import("long").Long | undefined;
    constant: import("long").Long | undefined;
    transactionBandwidthByte: import("long").Long | undefined;
    transactionVerification: import("long").Long | undefined;
    transactionRamByteHour: import("long").Long | undefined;
    transactionStorageByteHour: import("long").Long | undefined;
    contractTransactionGas: import("long").Long | undefined;
    transferVolumeHbar: import("long").Long | undefined;
    responseMemoryByte: import("long").Long | undefined;
    responseDiskByte: import("long").Long | undefined;
    /**
     * @internal
     * @returns {HashgraphProto.proto.IFeeComponents}
     */
    _toProtobuf(): HashgraphProto.proto.IFeeComponents;
    /**
     * @returns {Uint8Array}
     */
    toBytes(): Uint8Array;
}
import * as HashgraphProto from "@hashgraph/proto";
