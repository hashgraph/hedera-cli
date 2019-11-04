package com.hedera.cli.shell;

public class ProgressBar {
    private static final String CUU = "\u001B[A";
    private static final String DL = "\u001B[1M";

    private String doneMarker = "=";
    private String remainsMarker = "-";
    private String leftDelimiter = "<";
    private String rightDelimiter = ">";

    private ShellHelper shellHelper;

    private boolean started = false;

    public ProgressBar(ShellHelper shellHelper) {
        this.shellHelper = shellHelper;
    }

    public void display(int percentage) {
        display(percentage, null);
    }

    public void display(int percentage, String statusMessage) {
        if (!started) {
            started = true;
            shellHelper.getTerminal().writer().println();
        }
        int x = (percentage/5);
        int y = 20-x;
        String message = ((statusMessage == null) ? "" : statusMessage);

        String done = shellHelper.getSuccessMessage(new String(new char[x]).replace("\0", doneMarker));
        String remains = new String(new char[y]).replace("\0", remainsMarker);

        String progressBar = String.format("%s%s%s%s %d", leftDelimiter, done, remains, rightDelimiter, percentage);

        shellHelper.getTerminal().writer().println(CUU + "\r" + DL + progressBar + "% " + message);
        shellHelper.getTerminal().flush();
    }

    public void reset() {
        started = false;
    }

}
