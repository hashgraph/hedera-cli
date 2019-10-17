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
			// account create
			@ShellOption(value = { "-b", "--balance" }, defaultValue = "") String b,
			@ShellOption(value = { "-k", "--keygen" }, arity = 0) boolean k,
			@ShellOption(value = { "-m", "--method" }, defaultValue = "") String m,
			@ShellOption(value = { "-r", "--record" }, arity = 0) boolean r,
			// account delete
			@ShellOption(value = { "-o", "--oldAccount" }, defaultValue = "") String o,
			@ShellOption(value = { "-n", "--newAccount" }, defaultValue = "") String n) {

		String[] args = new String[]{};

		// @formatter:off
		if (subCommand.equals("create")) {
			ArrayList<String> argsList = new ArrayList<String>();
			if (!b.isEmpty()) argsList.add(b);
			argsList.add("-k=true");
			if (k) {
				argsList.add("-k=false");
			}
			if (!m.isEmpty()) argsList.add(m);
			argsList.add("-r=true");
			if (r) {
				argsList.add("-r=false");
			}
			Object[] objs = argsList.toArray();
			args = Arrays.copyOf(objs, objs.length, String[].class);
		}

		if (subCommand.equals("delete")) {
			ArrayList<String> argsList = new ArrayList<String>();
			if (!o.isEmpty()) argsList.add(o);
			if (!n.isEmpty()) argsList.add(n);
			Object[] objs = argsList.toArray();
			args = Arrays.copyOf(objs, objs.length, String[].class);
		}
		// @formatter:on

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
		if (!a.isEmpty()) argsList.add(a);
		if (!r.isEmpty()) argsList.add(r);
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