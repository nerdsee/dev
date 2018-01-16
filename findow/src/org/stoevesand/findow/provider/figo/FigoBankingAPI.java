package org.stoevesand.findow.provider.figo;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.jobs.JobManager;
import org.stoevesand.findow.jobs.TaskSolver;
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinTask;
import org.stoevesand.findow.model.FinToken;
import org.stoevesand.findow.model.FinTransactionList;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.ApiUser;
import org.stoevesand.findow.provider.BankingAPI;

import me.figo.FigoConnection;
import me.figo.FigoException;
import me.figo.FigoSession;
import me.figo.internal.TaskStatusResponse;
import me.figo.internal.TaskTokenResponse;
import me.figo.models.Bank;
import me.figo.models.Transaction;

public class FigoBankingAPI implements BankingAPI {

	private static final int BATCH_SIZE = 100;

	private Logger log = LoggerFactory.getLogger(FigoBankingAPI.class);

	private String client_id = "";
	private String client_secret = "";

	@Override
	public String importAccount(FinUser user, String bankId, String bankingUserId, String bankingPin) throws FinErrorHandler {
		FigoSession fs = new FigoSession(user.getToken());
		String taskToken = null;
		try {

			log.info("call setup new account");
			
			/* Hier wird bei FIGO ein Task gestartet, der das Konto importiert. Auf dem Task muss getTaskState aufgerufen
			 * werden, damit der Task bei FIGO losläuft 
			 */
			TaskTokenResponse er = fs.setupNewAccount(bankId, "de", bankingUserId, bankingPin, null, true, true);
			if (er != null) {
				
				// um den Status asynchron bei FIGO abzufragen, benötigt man den TaskToken
				// der ist in der Response enthalten, die man bei der Anlage aller Tasks zurückbekommt
				taskToken = er.getTaskToken();
				
				log.info("new task token for import: " + taskToken);
				
				// Für jeden Task bei FIGO gibt es einen "Schatten" bei findow.
				// hier ist auch der TaskToken gespeichert und der Typ des Tasks
				FinTask task = new FinTask(user, taskToken, TaskSolver.IMPORT_ACCOUNT);
				
				// erste Statusabfrage, damit der Request losläuft
				boolean changed = task.getTaskState(fs);
				if (changed) {
					task = PersistanceManager.getInstance().persist(task);
				}

				log.info("Import startet: " + task.getMessage());
				log.info("Account       : " + task.getSourceId());

				// der Task wird gescheduled, damit regelmäßig geprüft werden kann,
				// ob der Task bei FIGO fertig ist.
				JobManager.getInstance().addSingleTaskJob(task, new Date());

			} else {
				log.error("No TTR. Call failed.");
			}
		} catch (FigoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return taskToken;
	}

	private static FigoBankingAPI _instance = null;

	private FigoBankingAPI() {
		client_id = System.getProperty("figo_client_id");
		client_secret = System.getProperty("figo_client_secret");
	}

	public static BankingAPI getInstance() {
		if (_instance == null) {
			_instance = new FigoBankingAPI();
		}
		return _instance;
	}

	@Override
	public ApiUser createUser(String username, String password) throws FinErrorHandler {
		FigoConnection fc = new FigoConnection(client_id, client_secret, "http://localhost:3000");
		ApiUser apiUser = null;
		String email = "";
		String recoveryPassword = "";
		try {
			username = username.replace('|', '-');
			email = username + "@stoevesand.org";
			recoveryPassword = fc.addUser(username, email, password, "de");

			apiUser = new FigoUser(email, password, recoveryPassword);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FinErrorHandler(0, e.getMessage());
		} catch (FigoException e) {
			if (e.getErrorMessage().equals("user_exists")) {
				apiUser = new FigoUser(email, password, recoveryPassword);
			} else {
				throw new FinErrorHandler(e);
			}
		}

		return apiUser;
	}

	@Override
	public void deleteUser(String userToken) throws FinErrorHandler {
	}

	@Override
	public List<org.stoevesand.findow.model.FinBank> searchBanks(String search) {
		FigoConnection fc = new FigoConnection(client_id, client_secret, "http://localhost:3000");
		Bank bank = null;

		try {
			bank = fc.queryApi("/catalog/banks/de/" + search, null, "GET", Bank.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FigoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public FinToken requestUserToken(String username, String password) throws FinErrorHandler {

		return FigoTokenService.requestUserToken(client_id, client_secret, username, password);

	}

	@Override
	public boolean deleteAccount(FinUser user, FinAccount account) throws FinErrorHandler {
		FigoSession fs = new FigoSession(user.getToken());
		boolean success = false;
		try {
			fs.removeAccount(account.getSourceId());
			success = true;
		} catch (FigoException e) {
			log.error(e.getErrorMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	@Override
	public FinTransactionList searchTransactions(FinUser user, FinAccount account, int days) throws FinErrorHandler {
		
		FinTransactionList tl = new FinTransactionList();

		FigoSession fs = new FigoSession(user.getToken());

		try {
			String accountId = account == null ? null : account.getSourceId();
			// List<Transaction> txs = fs.getTransactions(accountId);

			int offset = 0;
			int count = BATCH_SIZE;
			int retCount = BATCH_SIZE;

			while (retCount == BATCH_SIZE) {

				List<Transaction> txs = fs.getTransactions(accountId, null, count, offset, null);

				retCount = txs.size();
				offset+=retCount;
				
				for (Transaction tx : txs) {
					org.stoevesand.findow.model.FinTransaction t = new org.stoevesand.findow.model.FinTransaction(user, account, tx);
					tl.addTransaction(t);
				}
			}

		} catch (FigoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tl;
	}

	@Override
	public void refreshAccount(FinUser user, FinAccount account) throws FinErrorHandler {
		FigoSession fs = new FigoSession(user.getToken());

		try {

			// prüfen, ob es noch einen laufen Task zu diesem Konto gibt.
			if (account.getTaskId() != null) {
				TaskStatusResponse tsr = fs.getTaskState(account.getTaskId());

				String accountId = tsr.getAccountId();
				log.info("Account: " + accountId);
				log.info("TSR: " + tsr);

				// wenn es zu dem Konto noch einen Task gibt,
				// dann lassen wir den laufen.
				if (!tsr.isEnded()) {
					return;
				}
			}

			me.figo.models.Account acc = fs.getAccount(account.getSourceId());

			account.refresh(acc);

		} catch (FigoException fe) {
			log.error("FIGO: " + fe.getMessage());
			throw new FinErrorHandler(fe);
		} catch (IOException e) {
			log.error("IO: " + e.getMessage());
		}

	}

	@Override
	public List<FinAccount> getAccounts(FinUser user) throws FinErrorHandler {
		FigoSession fs = new FigoSession(user.getToken());

		List<FinAccount> ret = new Vector<FinAccount>();

		try {

			List<me.figo.models.Account> accs = fs.getAccounts();

			log.info("Account@FIGO:");

			for (me.figo.models.Account acc : accs) {

				log.info(String.format("-> %s %s %s", acc.getAccountNumber(), acc.getAccountId(), acc.getBankName()));

				ret.add(new FinAccount(acc));
			}

		} catch (FigoException fe) {
			log.error("FIGO: " + fe.getMessage());
		} catch (IOException e) {
			log.error("IO: " + e.getMessage());
		}
		return ret;
	}

	/**
	 * Fordert eine Konten synchronisation an und startet dann einen Task, der
	 * wartet bis der synch durch ist. Dieser Task lädt am Ende erst die
	 * Transaktionen nach.
	 */
	@Override
	public void reloadAccountContent(FinUser user, FinAccount account) throws FinErrorHandler {
		FigoSession fs = new FigoSession(user.getToken());

		String taskToken = null;
		try {
			TaskTokenResponse er = fs.createSyncTask(user.getName(), "", null, null);
			if (er != null) {
				taskToken = er.getTaskToken();
				FinTask task = new FinTask(user, taskToken, TaskSolver.UPDATE_TX);
				// erste Statusabfrage, damit der Request losläuft
				boolean changed = task.getTaskState(fs);
				if (changed) {
					task = PersistanceManager.getInstance().persist(task);
				}

				log.info("Update Tx Task startet: " + task.getMessage());
				log.info("User: " + user);

				JobManager.getInstance().addSingleTaskJob(task, new Date());

			}
		} catch (FigoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// return taskToken;
	}

	@Override
	public String getClientId() {
		return client_id;
	}

	@Override
	public String getClientSecret() {
		return client_secret;
	}

}
