package com.hedera.cli.hedera.crypto;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.models.PreviewTransferList;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.HederaStatusException;
import com.hedera.hashgraph.sdk.account.AccountId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.util.concurrent.TimeoutException;

@Component
public class CryptoTransferPrompts {

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private InputReader inputReader;

    public boolean handlePromptPreview(AccountId operatorId, Map<Integer, PreviewTransferList> map)
            throws HederaStatusException, TimeoutException, InterruptedException {
        String isInfoCorrect = promptPreview(operatorId, map);
        if ("yes".equals(isInfoCorrect)) {
            shellHelper.print("Info is correct, senders will need to sign the transaction to release funds");
            // executeCryptoTransfer(operatorId);
            return true;
        }

        if ("no".equals(isInfoCorrect)) {
            shellHelper.print("Nope, incorrect, let's make some changes");
            return false;
        }

        shellHelper.printError("Input must be either yes or no");
        return false;
    }

    public String promptPreview(AccountId operatorId, Map<Integer, PreviewTransferList> previewTransferListMap) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String jsonStringTransferList = ow.writeValueAsString(previewTransferListMap);
            return inputReader.prompt("\nOperator\n" + operatorId + "\nTransfer List\n" + jsonStringTransferList
                    + "\n\nIs this correct?" + "\nyes/no");
        } catch (Exception e) {
            shellHelper.printError("Some error occurred");
            return null;
        }
    }

}