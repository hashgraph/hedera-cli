package com.hedera.cli.shell;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;


import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class ShellHelper {

    @Value("${shell.out.info}")
    public String infoColor = "CYAN";

    @Value("${shell.out.success}")
    public String successColor = "GREEN";

    @Value("${shell.out.warning}")
    public String warningColor = "YELLOW";

    @Value("${shell.out.error}")
    public String errorColor = "RED";

    @Getter
    private final Terminal terminal;

    /**
     * Construct colored message in the given color.
     *
     * @param message message to return
     * @param color   color to print
     * @return colored message
     */
    public String getColored(String message, PromptColor color) {
        // handle default values if message is null
        String finalMessage = "";
        if (message != null) finalMessage = message;
        // handle default attributedStyle if color is null
        AttributedStyle attributedStyle = AttributedStyle.DEFAULT.foreground(PromptColor.WHITE.toJlineAttributedStyle());
        if (color != null) {
            attributedStyle = AttributedStyle.DEFAULT.foreground(color.toJlineAttributedStyle());
        }
        // build our attributed string with message and attributedStyle
        AttributedStringBuilder asb = new AttributedStringBuilder();
        asb.append(finalMessage, attributedStyle);
        return asb.toAnsi();
    }

    public String getInfoMessage(String message) {
        return getColored(message, PromptColor.valueOf(infoColor));
    }

    public String getSuccessMessage(String message) {
        return getColored(message, PromptColor.valueOf(successColor));
    }

    public String getWarningMessage(String message) {
        return getColored(message, PromptColor.valueOf(warningColor));
    }

    public String getErrorMessage(String message) {
        return getColored(message, PromptColor.valueOf(errorColor));
    }

    //--- Print methods -------------------------------------------------------

    /**
     * Print message to the console in the default color.
     *
     * @param message message to print
     */
    public void print(String message) {
        print(message, null);
    }

    /**
     * Print message to the console in the success color.
     *
     * @param message message to print
     */
    public void printSuccess(String message) {
        print(message, PromptColor.valueOf(successColor));
    }

    /**
     * Print message to the console in the info color.
     *
     * @param message message to print
     */
    public void printInfo(String message) {
        print(message, PromptColor.valueOf(infoColor));
    }

    /**
     * Print message to the console in the warning color.
     *
     * @param message message to print
     */
    public void printWarning(String message) {
        print(message, PromptColor.valueOf(warningColor));
    }

    /**
     * Print message to the console in the error color.
     *
     * @param message message to print
     */
    public void printError(String message) {
        print(message, PromptColor.valueOf(errorColor));
    }

    /**
     * Generic Print to the console method.
     *
     * @param message message to print
     * @param color   (optional) prompt color
     */
    public void print(String message, PromptColor color) {
        String toPrint = message;
        if (color != null) {
            toPrint = getColored(message, color);
        }
        if (terminal != null) {
            terminal.writer().println(toPrint);
            terminal.flush();
        }
    }

    /**
     * Helper method for long running commands like subscribe-topic.
     * Current thread blocks to wait for user interrupt (Ctrl+C). So this function
     * should be called directly from the thread which invokes @ShellMethod
     * annotated function. That would prevent the control from getting back to the
     * Spring Shell (until user interrupts manually) and the long-running command
     * can continue to write to the terminal.
     * This is NOT same as interactive command since all user inputs (except Ctrl+C)
     * will be ignored.
     * @param beforeWait is run before waiting for user interrupt
     * @param afterInterrupt is run after user interrupts. Resources that are setup in
     *                       beforeWait MUST be cleaned up here.
     */
    public void waitForUserInterrupt(Runnable beforeWait, Runnable afterInterrupt) {
        AtomicBoolean userInterrupted = new AtomicBoolean(false);
        Object monitor = new Object();

        // Register handler for Ctrl+C from user
        this.terminal.handle(Terminal.Signal.INT, signal -> {
            synchronized (monitor) {
                userInterrupted.set(true);
                monitor.notify();
            }
        });

        // Command setup
        beforeWait.run();

        // Let the command run until interrupted by user
        synchronized (monitor) {
            while(!userInterrupted.get()) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }

        // Command tear down
        afterInterrupt.run();
    }
}
