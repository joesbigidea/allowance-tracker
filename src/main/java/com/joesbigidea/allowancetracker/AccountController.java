package com.joesbigidea.allowancetracker;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by joe on 2/13/16.
 */
public class AccountController {

    public BigDecimal getBalance() {
        List<Transaction> allowanceTrans = PersistenceService.apply(em ->
                em.createQuery("from Transaction where allowance = true", Transaction.class).getResultList());

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
        PersistenceService.applyTransacted(em -> {
            Date lastAllowancePosted = em.createQuery("select max(postedDate) from Transaction where allowance = true", Date.class).getSingleResult();
            Period allowancePeriod = Period.ofWeeks(1);
            Instant nextAllowance = lastAllowancePosted.toInstant().plus(allowancePeriod);
            List<Transaction> newAllowanceTransactions = new ArrayList<>();
            while (nextAllowance.isBefore(Instant.now())) {
                newAllowanceTransactions.add(Transaction.allowance(new Date(nextAllowance.toEpochMilli())));
                nextAllowance = nextAllowance.plus(allowancePeriod);
            }
            return PersistenceService.persist(newAllowanceTransactions.toArray());
        });


    }
}
