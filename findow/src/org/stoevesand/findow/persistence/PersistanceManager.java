package org.stoevesand.findow.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.hint.CategoryRule;
import org.stoevesand.findow.hint.RegexHint;
import org.stoevesand.findow.model.FinAccount;
import org.stoevesand.findow.model.FinBank;
import org.stoevesand.findow.model.FinCategory;
import org.stoevesand.findow.model.FinCategorySum;
import org.stoevesand.findow.model.FinErrorHandler;
import org.stoevesand.findow.model.FinTask;
import org.stoevesand.findow.model.FinTransaction;
import org.stoevesand.findow.model.FinTransactionList;
import org.stoevesand.findow.model.FinUser;
import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.server.FindowSystem;

public class PersistanceManager {

	private EntityManagerFactory entityManagerFactory;

	private static PersistanceManager _instance = null;

	private Logger log = LoggerFactory.getLogger(PersistanceManager.class);

	public static PersistanceManager getInstance() {
		if (_instance == null) {
			_instance = new PersistanceManager();
		}
		return _instance;
	}

	private PersistanceManager() {

		if ("prod".equals(FindowSystem.getStage())) {
			// PROD
			entityManagerFactory = Persistence.createEntityManagerFactory("org.stoevesand.finapi.persistence.prod");
			log.info("Using PROD PersistanceUnit");
		} else {
			// DEV
			entityManagerFactory = Persistence.createEntityManagerFactory("org.stoevesand.finapi.persistence.dev");
			log.info("Using DEV PersistanceUnit");
		}

	}

	public void storeTx(List<FinTransaction> transactionList) {
		// create a couple of events...
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		for (FinTransaction t : transactionList) {
			persist(entityManager, t);
		}

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	public void storeAccounts(List<FinAccount> accountList) {
		// create a couple of events...
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		for (FinAccount t : accountList) {
			persist(entityManager, t);
		}

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	public List<FinTransaction> getTx(FinUser user, Long accountId, int days) throws FinErrorHandler {

		// create a couple of events...
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();

		List<FinTransaction> result = new Vector<FinTransaction>();

		if (accountId != null) {
			// TODO: wieder heil machen
			FinAccount account = user.getAccount(accountId);
			// Standardfall mit echter accountId
			// Account account = entityManager.find(Account.class, accountId);
			if (account != null) {
				long endMillis = System.currentTimeMillis();
				long startMillis = endMillis - (1000*60*60*24*days);
				Date endDate = new Date(endMillis);
				Date startDate = new Date(startMillis);
						
				List<FinTransaction> subResult = em.createQuery("select t from Transaction t where t.accountId=:aid and t.bookingDate between :startDate and :endDate order by t.bookingDate desc", FinTransaction.class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("aid", account.getId()).getResultList();
				result.addAll(subResult);
			} else {
				throw new FinErrorHandler(500, "NO SUCH ACCOUNT");
			}

		} else {
			// wenn keine accountId angegeben ist, dann werden die Tx von allen
			// Accounts geladen
			List<FinAccount> accounts = em.createQuery("select a from Account a where user=:id", FinAccount.class).setParameter("id", user).getResultList();
			if (accounts != null) {
				for (FinAccount account : accounts) {
					List<FinTransaction> subResult = em.createQuery("select t from Transaction t where t.accountId=:aid and t.bookingDate > current_date - :daydelta", FinTransaction.class).setParameter("daydelta", days).setParameter("aid", account.getId()).getResultList();
					result.addAll(subResult);
				}
			}
		}

		em.getTransaction().commit();
		em.close();

		result.sort(FinTransaction.bookingDateComparatorDesc);

		return result;
	}

	public List<FinTransaction> getTx() throws FinErrorHandler {

		// create a couple of events...
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();

		List<FinTransaction> result = new Vector<FinTransaction>();

		List<FinTransaction> subResult = em.createQuery("select t from Transaction t where t.category!=null", FinTransaction.class).getResultList();
		result.addAll(subResult);

		em.getTransaction().commit();
		em.close();

		return result;
	}

	public FinUser getUserByName(String id) {
		FinUser ret = null;
		// create a couple of events...
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<FinUser> result = entityManager.createQuery("select t from User t where t.name = :username", FinUser.class).setParameter("username", id).getResultList();
		if (result.size() > 0) {
			ret = (FinUser) result.get(0);
		}

		entityManager.getTransaction().commit();
		entityManager.close();

		return ret;
	}

	public FinUser getUserByExternalName(String id) {
		FinUser ret = null;
		// create a couple of events...
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<FinUser> result = entityManager.createQuery("select t from User t where t.backendName = :username", FinUser.class).setParameter("username", id).getResultList();
		if (result.size() > 0) {
			ret = (FinUser) result.get(0);
		}
		entityManager.getTransaction().commit();
		entityManager.close();

		return ret;
	}

	public void store(Object obj) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(obj);
		entityManager.getTransaction().commit();
	}

	public void remove(EntityManager em, Object entity) {
		em.remove(em.contains(entity) ? entity : em.merge(entity));
	}

	public <T> void persistList(List<T> entityList) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		for (T entity : entityList) {
			persist(entityManager, entity);
		}

		entityManager.flush();
		entityManager.getTransaction().commit();
		entityManager.close();

	}

	public <T> T persist(T entity) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entity = persist(entityManager, entity);
		entityManager.flush();
		entityManager.getTransaction().commit();
		entityManager.close();
		return entity;
	}

	public <T> T persist(EntityManager em, T entity) {
		entity = em.merge(entity);
		em.persist(entity);
		// em.persist(em.contains(entity) ? entity : em.merge(entity));
		return entity;
	}

	public FinTransaction getTxByExternalId(String sourceId) {
		FinTransaction ret = null;

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<FinTransaction> result = entityManager.createQuery("select t from Transaction t where t.sourceId=:sourceid").setParameter("sourceid", sourceId).getResultList();
		if (result.size() > 0) {
			ret = (FinTransaction) result.get(0);
		}

		entityManager.getTransaction().commit();
		entityManager.close();

		return ret;
	}

	public FinCategory getCategory(FinCategory category) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		FinCategory ret = entityManager.find(FinCategory.class, category.getId());

		if (ret == null) {
			entityManager.persist(category);
			ret = category;
		}

		entityManager.getTransaction().commit();
		entityManager.close();

		return ret;
	}

	public FinUser getUser(long userId) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		FinUser ret = entityManager.find(FinUser.class, userId);

		entityManager.getTransaction().commit();
		entityManager.close();

		return ret;
	}

	public FinCategory getCategory(long categoryId) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		FinCategory ret = entityManager.find(FinCategory.class, categoryId);

		entityManager.getTransaction().commit();
		entityManager.close();

		return ret;
	}

	public List<FinCategorySum> getCategorySummary(FinUser user, Long accountId, int days) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		FinAccount account = user.getAccount(accountId);

		Query q = entityManager.createNativeQuery("select coalesce(category_id,9999) as category_id, count(*) as count, sum(t.AMOUNT_CENT) as sum from FINDOW.TRANSACTIONS t where t.account_id=:aid group by t.category_id", FinCategorySum.class).setParameter("aid", account.getId());
		List<FinCategorySum> catsum = q.getResultList();

		entityManager.close();
		return catsum;
	}

	public void deleteUserByName(String id) {
		FinUser ret = null;
		// create a couple of events...
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		List<FinUser> result = em.createQuery("select t from User t where t.name = :username", FinUser.class).setParameter("username", id).getResultList();
		if (result.size() > 0) {
			ret = (FinUser) result.get(0);
		}
		em.remove(ret);
		em.getTransaction().commit();
		em.close();
	}

	public List<FinUser> getUsers() {

		EntityManager em = entityManagerFactory.createEntityManager();
		// em.getTransaction().begin();

		List<FinUser> users = em.createQuery("select u from User u", FinUser.class).getResultList();

		// DEBUG
		for (FinUser user : users) {
			log.info("User: " + user.getClass() + " " + user);
		}

		// em.getTransaction().commit();
		em.close();

		return users;
	}

	public List<FinCategory> getCategories() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<FinCategory> accounts = entityManager.createQuery("select c from Category c", FinCategory.class).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		return accounts;
	}

	public List<CategoryRule> getCategoryRules() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<CategoryRule> rules = entityManager.createQuery("select c from CategoryRule c", CategoryRule.class).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		return rules;
	}

	public List<FinAccount> getAllAccounts() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<FinAccount> accounts = entityManager.createQuery("select a from Account a", FinAccount.class).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		return accounts;
	}

	public List<FinAccount> getAccounts(FinUser user) throws FinErrorHandler {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<FinAccount> accounts = entityManager.createQuery("select a from Account a where a.user=:user", FinAccount.class).setParameter("user", user).getResultList();

		boolean refresh = false;
		if (refresh) {
			for (FinAccount account : accounts) {
				BankingAPI api = FindowSystem.getBankingAPI(user);
				try {
					api.refreshAccount(user, account);
					entityManager.persist(account);
				} catch (FinErrorHandler e) {
					log.error("Failed to refresh account. Removed account: " + account.getId());
					user.removeAccount(account);
					persist(entityManager, user);
				}
			}

			// am ende nochmal neu laden, damit alle neue und entfernten
			// Accounts
			// richtig sind.
			accounts = entityManager.createQuery("select a from Account a where a.user=:user", FinAccount.class).setParameter("user", user).getResultList();
		}
		entityManager.getTransaction().commit();
		entityManager.close();

		return accounts;
	}

	public List<RegexHint> getRegexHints() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<RegexHint> hints = entityManager.createQuery("select a from RegexHint a", RegexHint.class).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		return hints;
	}

	public FinAccount getAccountByExternalId(FinUser user, String accountId) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		FinAccount retAccount = null;

		List<FinAccount> result = entityManager.createQuery("select t from Account t where t.sourceId = :sid", FinAccount.class).setParameter("sid", accountId).getResultList();

		if (result.size() > 0) {

			if (result.size() > 1) {
				log.error("Multiple accounts with identical external id.");
			}

			retAccount = result.get(0);

		} else {
			log.info("No account with external id " + accountId);
		}
		entityManager.getTransaction().commit();
		entityManager.close();

		return retAccount;
	}

	public FinAccount getAccount(FinUser user, Long accountId) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		// FinAccount retAccount = null;

		FinAccount account = entityManager.find(FinAccount.class, accountId);
		// if (account != null) {
		//
		// try {
		// BankingAPI api = FindowSystem.getBankingAPI(user);
		// api.refreshAccount(user, account);
		// } catch (FinErrorHandler e) {
		// e.printStackTrace();
		// }
		//
		// int ret = 0;
		//
		// if (ret == 404) {
		// user.removeAccount(account);
		// remove(entityManager, account);
		// persist(entityManager, user);
		// } else {
		// entityManager.persist(account);
		// retAccount = account;
		// }
		// }

		entityManager.getTransaction().commit();
		entityManager.close();

		return account;
	}

	public FinAccount getAccount(Long accountId) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		FinAccount account = entityManager.find(FinAccount.class, accountId);

		entityManager.getTransaction().commit();
		entityManager.close();

		return account;
	}

	public void deleteAccount(FinUser user, Long accountId) throws FinErrorHandler {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		user.removeAccount(accountId);
		persist(entityManager, user);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	public void deleteAccounts(FinUser user, int connectionId) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<FinAccount> accounts = entityManager.createQuery("select a from Account a where a.bankConnectionId=:bcid", FinAccount.class).setParameter("bcid", connectionId).getResultList();

		for (FinAccount account : accounts) {
			user.removeAccount(account);
		}
		persist(entityManager, user);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	public void checkAccount(FinUser user, FinAccount account) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<FinAccount> accounts = entityManager.createQuery("select a from Account a where a.sourceId=:sid", FinAccount.class).setParameter("sid", account.getSourceId()).getResultList();

		if (accounts.isEmpty()) {
			account.setUser(user);
			persist(entityManager, account);
		}
		entityManager.getTransaction().commit();
		entityManager.close();

	}

	public void checkAccounts(FinUser user, List<FinAccount> accounts) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		for (FinAccount account : accounts) {

			List<FinAccount> accs = entityManager.createQuery("select a from Account a where a.sourceId=:sid", FinAccount.class).setParameter("sid", account.getSourceId()).getResultList();

			if (accs.isEmpty()) {
				user.addAccount(account);
			}
		}
		persist(entityManager, user);
		entityManager.getTransaction().commit();
		entityManager.close();

	}

	public List<FinTask> getActiveTasks() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<FinTask> accounts = entityManager.createQuery("select a from Task a where a.active", FinTask.class).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		return accounts;
	}

	public void updateTransactions(FinUser user, FinAccount account, int days) throws FinErrorHandler {

		log.info("Update account " + account + " - days: " + days);

		account.setLastUpdateAttempt(new Date());
		persist(account);

		List<FinTransaction> newTransactions = new Vector<FinTransaction>();
		FinTransactionList transactions = null;

		int totalTx = 1 - 1;

		BankingAPI bankingAPI = FindowSystem.getBankingAPI(user);
		transactions = bankingAPI.searchTransactions(user, account, days);

		if (transactions.getTransactions() != null) {
			totalTx = transactions.getTransactions().size();
			for (FinTransaction tx : transactions.getTransactions()) {
				FinTransaction knownTx = getTxByExternalId(tx.getSourceId());
				if (knownTx == null) {
					tx.lookForCategory();
					tx.lookForHints();
					newTransactions.add(tx);
				}
			}
		}

		log.info("Transactions [new/total]: [" + newTransactions.size() + "/" + totalTx + "]");

		if (newTransactions.size() > 0) {
			log.info("account updated");
			storeTx(newTransactions);
		} else {
			log.info("No update.");
		}

		account.setLastSuccessfulUpdate(new Date());
		persist(account);

	}

	public void updateTransactionCategories() {

		log.info("Update categories.");

		List<FinTransaction> changedTransactions = new Vector<FinTransaction>();
		List<FinTransaction> transactions = getTxWithoutCategory();

		for (FinTransaction tx : transactions) {
			FinCategory cat = tx.lookForCategory();
			if (cat != null) {
				changedTransactions.add(tx);
			}
		}

		if (changedTransactions.size() > 0) {
			log.info("transactions updated: " + changedTransactions.size());
			storeTx(changedTransactions);
		} else {
			log.info("No update.");
		}

	}

	private List<FinTransaction> getTxWithoutCategory() {

		// create a couple of events...
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();

		List<FinTransaction> result = new Vector<FinTransaction>();

		List<FinTransaction> subResult = em.createQuery("select t from Transaction t where t.category is null", FinTransaction.class).getResultList();
		result.addAll(subResult);

		em.getTransaction().commit();
		em.close();

		return result;
	}

	public List<FinBank> searchBanks(String search) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		List<FinBank> accounts = entityManager.createQuery("select a from Bank a where UPPER(a.name) like :search", FinBank.class).setParameter("search", "%" + search.toUpperCase() + "%").getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();

		return accounts;
	}

	public FinBank getBank(int bankId) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		FinBank bank = entityManager.find(FinBank.class, bankId);

		entityManager.getTransaction().commit();
		entityManager.close();

		return bank;
	}

	public FinBank getBankByCode(String bankCode) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		FinBank retBank = null;

		List<FinBank> result = entityManager.createQuery("select t from Bank t where t.blz = :bc", FinBank.class).setParameter("bc", bankCode).getResultList();

		if (result.size() > 0) {

			if (result.size() > 1) {
				log.error("Multiple banks with identical bank code: " + bankCode);
			}

			retBank = result.get(0);

		} else {
			log.info("No bank with bank code" + bankCode);
		}
		entityManager.getTransaction().commit();
		entityManager.close();

		return retBank;
	}

}
