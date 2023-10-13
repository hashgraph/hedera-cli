const {
    TokenSupplyType,
  } = require("@hashgraph/sdk");

function getSupplyType(type) {
    const tokenType = type.toLowerCase();
    if (tokenType === "finite") {
        return TokenSupplyType.Finite;
    } else if (tokenType === "infinite") {
        return TokenSupplyType.Infinite;
    } else {
        throw new Error("Invalid supply type");
    }
}

module.exports = {
    getSupplyType,
};