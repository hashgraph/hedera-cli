package com.hedera.cli.shell;

import org.jline.terminal.Terminal;
import org.springframework.stereotype.Component;

@Component
public class SignalHandler implements Terminal.SignalHandler {
    public final Terminal terminal;

    public SignalHandler(Terminal terminal) {
        this.terminal = terminal;
        for (Terminal.Signal signal : Terminal.Signal.values()) {
            this.terminal.handle(signal, this);
        }
    }

    @Override
    public void handle(Terminal.Signal signal) {
        terminal.writer().print("Signal = " + signal);
    }
}
