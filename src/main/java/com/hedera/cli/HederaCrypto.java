package com.hedera.cli;

import java.util.ArrayList;
import java.util.Arrays;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.hedera.crypto.Account;
import com.hedera.cli.hedera.crypto.Transfer;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@ShellComponent
public class HederaCrypto extends CliDefaults {

	@Autowired
	ApplicationContext context;

	@Autowired
	ShellHelper shellHelper;

	@Autowired
	InputReader inputReader;

	@Autowired
	Account account;

	@ShellMethodAvailability("isDefaultNetworkAndAccountSet")
	@ShellMethod(value = "manage Hedera account")
	public void account(@ShellOption(defaultValue = "") String subCommand,
			// account use
			@ShellOption(defaultValue = "") String accountId,
			// Specifying -y flag will set y to be true (which will skip the preview)
			@ShellOption(value = { "-y", "--yes"}, defaultValue = "false") boolean y,
			// account create
			@ShellOption(value = { "-b", "--balance" }, defaultValue = "") String b,
			@ShellOption(value = { "-k", "--keygen" }, defaultValue = "false") boolean k,
			@ShellOption(value = { "-m", "--method" }, defaultValue = "") String m,
			@ShellOption(value = { "-r", "--record" }, defaultValue = "false") boolean r,
			// account delete
			@ShellOption(value = { "-o", "--oldAccount" }, defaultValue = "") String o,
			@ShellOption(value = { "-n", "--newAccount" }, defaultValue = "") String n) {

		// convert our Spring Shell arguments into an argument list that PicoCli can use.
		String[] args = null;
		ArrayList<String> argsList = new ArrayList<String>();
		Object[] objs;

		switch (subCommand) {
			case "create":
				if (y) argsList.add("-y");
				if (!b.isEmpty()) argsList.add("-b " + b);
				argsList.add("-k " + k);
				if (!m.isEmpty()) argsList.add("-m " + m);
				argsList.add("-r " + r);
				objs = argsList.toArray();
				args = Arrays.copyOf(objs, objs.length, String[].class);
				break;
			case "update":
				break;
			case "info":
				if(!accountId.isEmpty()) argsList.add(accountId);
				objs = argsList.toArray();
				args = Arrays.copyOf(objs, objs.length, String[].class);
				break;
			case "delete":
				if (y) argsList.add("-y");
				if (!o.isEmpty()) argsList.add("-o " + o);
				if (!n.isEmpty()) argsList.add("-n " + n);
				objs = argsList.toArray();
				args = Arrays.copyOf(objs, objs.length, String[].class);
				break;
			case "recovery":
				break;
			case "ls":
				break;
			case "use":
				if (!accountId.isEmpty()) argsList.add(accountId);
				objs = argsList.toArray();
				args = Arrays.copyOf(objs, objs.length, String[].class);
				break;
			default:
				break;
		}
		// Pass args onwards and invoke our PicoCli classes
		Account account = new Account();
		account.handle(context, inputReader, subCommand, args);
	}

	@ShellMethodAvailability("isDefaultNetworkAndAccountSet")
	@ShellMethod(value = "transfer hbars from one hedera account to another")
	public void transfer(@ShellOption(defaultValue = "") String subCommand,
			@ShellOption(value = { "-a", "--accountId" }, defaultValue = "") String a,
			@ShellOption(value = { "-r", "--recipientAmount" }, defaultValue = "") String r,
			@ShellOption(value = { "-n", "--noPreview" }, arity = 0) boolean n) {

		// @formatter:on
		ArrayList<String> argsList = new ArrayList<String>();
		if (!a.isEmpty()) argsList.add("-a=" + a);
		if (!r.isEmpty()) argsList.add("-r=" + r);
		argsList.add("-n=yes");
		if (n) {
			argsList.add("-n=no");
		}
		Object[] objs = argsList.toArray();
		String[] args = Arrays.copyOf(objs, objs.length, String[].class);
		// @formatter:off

		Transfer transfer = new Transfer();
		try {
			transfer.handle(context, inputReader, subCommand, args);
		} catch (Exception e) {
			e.printStackTrace();
			// print out a useful message for end user here
		}
	}

}