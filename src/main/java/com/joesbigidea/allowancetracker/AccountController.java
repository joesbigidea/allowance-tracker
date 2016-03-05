package com.joesbigidea.allowancetracker;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by joe on 2/13/16.
 */
public class AccountController {

    public BigDecimal getBalance() {
        updateAllowanceTransactions();
        return PersistenceService.apply(em -> em.createQuery("select sum(amount) from Transaction", BigDecimal.class).getSingleResult());
    }

    public List<Transaction> getTransactions() {
        updateAllowanceTransactions();
        return PersistenceService.apply(em -> em.createQuery("from Transaction order by postedDate DESC", Transaction.class).getResultList());
    }

    public Transaction addTransaction(Transaction t) {
        Preconditions.checkNotNull(t, "Transaction cannot be null");
        Preconditions.checkNotNull(t.getAmount(), "Transaction amount cannot be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(t.getDescription()), "Transaction description must be provided");
        Preconditions.checkNotNull(t.getPostedDate(), "Transaction posted date must be provided");
        return PersistenceService.persist(t);
    }

    public boolean deleteTransaction(long id) {
        return PersistenceService.applyTransacted(em -> {
            Transaction transaction = em.find(Transaction.class, id);
            if (transaction != null) {
                em.remove(transaction);
                return true;
            }
            return false;
        });
    }

    private void updateAllowanceTransactions() {
        boolean updated = false;
        while (!updated) {
            try {
                PersistenceService.applyTransacted(em -> {
                    //use the allowanceLock as a lock to prevent double posting allowance
                    AllowanceLock.getLock(em);
                    Date lastAllowancePosted = em.createQuery("select max(postedDate) from Transaction where allowance = true", Date.class).getSingleResult();
                    Period allowancePeriod = Period.ofWeeks(1);
                    Instant nextAllowance = lastAllowancePosted.toInstant().plus(allowancePeriod);
                    List<Transaction> newAllowanceTransactions = new ArrayList<>();
                    while (nextAllowance.isBefore(Instant.now())) {
                        Transaction allowance = Transaction.allowance(new Date(nextAllowance.toEpochMilli()));
                        newAllowanceTransactions.add(allowance);
                        LoggerFactory.getLogger(getClass()).info("Creating allowance transction for: " + allowance.getPostedDate());
                        nextAllowance = nextAllowance.plus(allowancePeriod);
                    }
                    if (!newAllowanceTransactions.isEmpty()) {
                        newAllowanceTransactions.forEach(em::merge);
                    }
                    return null;
                });
                updated = true;
            }
            catch (OptimisticLockException e) {
                LoggerFactory.getLogger(getClass()).info("Somebody else is updating the account, we'll try again.");
            }
        }
    }
}
