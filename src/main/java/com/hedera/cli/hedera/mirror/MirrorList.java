package com.hedera.cli.hedera.mirror;

import com.hedera.cli.models.AddressBookManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;

@Component
@Command(name = "ls", description = "@|fg(225) List all available Hedera mirror.|@", helpCommand = true)
public class MirrorList implements Runnable {

    @Autowired
    private AddressBookManager addressBookManager;

    @Override
    public void run() {
        addressBookManager.listMirrorNodes();
    }

}