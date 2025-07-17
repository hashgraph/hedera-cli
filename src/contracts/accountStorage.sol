// SPDX-License-Identifier: MIT
pragma solidity ^0.8.22;

contract HederaAccountStorage {

    // Array to store Hedera account IDs as strings
    string[] private accountIds;

    // Event emitted when an account ID is added
    event AccountIdAdded(string accountId);

    // Function to add a new Hedera account ID
    function addAccountId(string memory _accountId) public {
        accountIds.push(_accountId);
        emit AccountIdAdded(_accountId);
    }

    // Function to retrieve all stored Hedera account IDs
    function getAccountIds() public view returns (string[] memory) {
        return accountIds;
    }

    // Function to retrieve a specific account ID by index
    function getAccountId(uint index) public view returns (string memory) {
        require(index < accountIds.length, "Index out of bounds");
        return accountIds[index];
    }

    // Function to get the total number of account IDs stored
    function getTotalAccountIds() public view returns (uint) {
        return accountIds.length;
    }
}