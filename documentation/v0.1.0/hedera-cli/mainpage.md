#Hedera CLI Tool
Learning made easy for developers :)

| Main Command | SubCommand | Parameters | Description |
| :--- | --- | :---: | ---|
|`network` | `ls` | - | List all networks 
|          | `use` |`0.0.xxxx` | Sets the current network |
|`account` | `ls` | - | List all accounts 
|          | `use` |`0.0.xxxx` | Sets the accountId as *current operator* |

### Hedera Services

#### Account
| Main Command| SubCommand | Parameters | Description |
| :--- | :--- | :--- | ---|
| `account`| `info` | `0.0.xxxx` | Gets the info of accountId. |
| | `balance` | `0.0.xxxx` | Gets the balance of accountId. |
| | `create` | `-b` tinybars | Default creates a new account with balance **[-b]**, new mnemonic and keypair. |
| | | `-k` keygen | When **[-k]** is specified, keygen = false, so creates a new account associated with current operator's public key. | 
| | `update` | | Updates the keypair of account. |
| `delete` | | `-o` oldAccountId | Deletes the old account **[-o]** and transfer remaining funds into new account **[-n]**. |
| | | `-n` newAccountId | 
| | | `-y` yesSkipPreview | Skips preview and execute transaction.
| `transfer` | - | `-s` sender | Transfer to single recipient. |
| |-| `-r` recipient | Transfer to multiple recipients. |
| |-| `-tb` amountInTinybars | Transfer from 1 operator, 1 sender to single/multiple recipients. |
| |-| `-hb` amountInHbars | Transfer from multiple senders to single/multiple recipients. |
| | | `-y` yesSkipPreview | Skips preview and execute transaction. |

#### File
| Main Command| SubCommand | Parameters | Description |
| :--- | --- | --- | ---|
| file | | |

#### Smart Contract
| Main Command| Command | Parameters | Description |
| :--- | --- | --- | ---|
| contract |