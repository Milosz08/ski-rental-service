/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.user_details;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;

@RequiredArgsConstructor
public class UserDetailsDao implements IUserDetailsDao {

    private final Session session;

    @Override
    public boolean checkIfCustomerWithSamePeselExist(String pesel, Object customerId) {
        final String jpqlFindPesel = """
                SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d
                WHERE d.pesel = :pesel AND c.id <> :cid
            """;
        return session.createQuery(jpqlFindPesel, Boolean.class)
            .setParameter("pesel", pesel)
            .setParameter("cid", customerId)
            .getSingleResult();
    }

    @Override
    public boolean checkIfCustomerWithSameEmailExist(String email, Object customerId) {
        final String jpqlFindEmailAddress = """
                SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d
                WHERE d.emailAddress = :emailAddress AND c.id <> :cid
            """;
        return session.createQuery(jpqlFindEmailAddress, Boolean.class)
            .setParameter("emailAddress", email)
            .setParameter("cid", customerId)
            .getSingleResult();
    }

    @Override
    public boolean checkIfCustomerWithSamePhoneNumberExist(String phoneNumber, Object customerId) {
        final String jpqlFindPhoneNumber = """
                SELECT COUNT(c.id) > 0 FROM CustomerEntity c INNER JOIN c.userDetails d
                WHERE d.phoneNumber = :phoneNumber AND c.id <> :cid
            """;
        return session.createQuery(jpqlFindPhoneNumber, Boolean.class)
            .setParameter("phoneNumber", phoneNumber)
            .setParameter("cid", customerId)
            .getSingleResult();
    }

    @Override
    public boolean checkIfEmployerWithSamePeselExist(String pesel, Object employerId) {
        final String jpqlFindPesel = """
                SELECT COUNT(e.id) > 0 FROM EmployerEntity e INNER JOIN e.userDetails d
                WHERE d.pesel = :pesel AND e.id <> :eid
            """;
        return session.createQuery(jpqlFindPesel, Boolean.class)
            .setParameter("pesel", pesel)
            .setParameter("eid", employerId)
            .getSingleResult();
    }

    @Override
    public boolean checkIfEmployerWithSamePhoneNumberExist(String phoneNumber, Object employerId) {
        final String jpqlFindPhoneNumber = """
                SELECT COUNT(e.id) > 0 FROM EmployerEntity e INNER JOIN e.userDetails d
                WHERE d.phoneNumber = :phoneNumber AND e.id <> :eid
            """;
        return session.createQuery(jpqlFindPhoneNumber, Boolean.class)
            .setParameter("phoneNumber", phoneNumber)
            .setParameter("eid", employerId)
            .getSingleResult();
    }
}
