import { TokenSupplyType } from "@hashgraph/sdk";

const getSupplyType = (type: ("finite"|"infinite")): TokenSupplyType => {
    const tokenType = type.toLowerCase();
    if (tokenType === "finite") {
        return TokenSupplyType.Finite;
    } else if (tokenType === "infinite") {
        return TokenSupplyType.Infinite;
    } else {
        throw new Error("Invalid supply type");
    }
}

export {
    getSupplyType,
};