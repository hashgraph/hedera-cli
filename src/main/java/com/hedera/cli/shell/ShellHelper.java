package com.hedera.cli.shell;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ShellHelper {

    @Value("${shell.out.info}")
    public String infoColor = "CYAN";

    @Value("${shell.out.success}")
    public String successColor = "GREEN";

    @Value("${shell.out.warning}")
    public String warningColor = "YELLOW";

    @Value("${shell.out.error}")
    public String errorColor = "RED";

    private Terminal terminal;

    public ShellHelper(Terminal terminal) {
        this.terminal = terminal;
    }

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

    //--- set / get methods ---------------------------------------------------

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }
}
