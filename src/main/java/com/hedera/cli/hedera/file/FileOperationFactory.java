package com.hedera.cli.hedera.file;

import com.hedera.cli.hedera.crypto.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class FileOperationFactory {

    @Autowired
    private FileInfo fileInfo;

    @Autowired
    private FileCreate fileCreate;

    @Autowired
    private FileDelete fileDelete;

    private Map<String, Operation> operationMap = new HashMap<>();

    @PostConstruct
    public void init() {
        operationMap.put("info", fileInfo);
        operationMap.put("create", fileCreate);
        operationMap.put("delete", fileDelete);
    }

    public Optional<Operation> getOperation(String operator) {
        return Optional.ofNullable(operationMap.get(operator));
    }
}
