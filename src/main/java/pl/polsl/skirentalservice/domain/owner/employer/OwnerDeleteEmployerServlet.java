/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerDeleteEmployerServlet.java
 *  Last modified: 31/01/2023, 08:25
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.employer;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.ssh.*;
import pl.polsl.skirentalservice.dao.employer.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.ssh.SshSocketBean;

import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/delete-employer")
public class OwnerDeleteEmployerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerAddEmployerServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private SshSocketBean sshSocket;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final IEmployerDao employerDao = new EmployerDao(session);

                final var deletingEmployer = employerDao.findEmployerBasedId(userId).orElseThrow(() -> {
                    throw new UserNotFoundException(userId);
                });
                if (employerDao.checkIfEmployerHasOpenedRents(userId)) throw new EmployerHasOpenedRentsException();

                final IExecCommandPerformer commandPerformer = new ExecCommandPerformer(sshSocket);
                commandPerformer.deleteMailbox(deletingEmployer.getEmailAddress());
                session.remove(deletingEmployer);

                alert.setType(INFO);
                alert.setMessage(
                    "Pomyślnie usunięto pracownika z ID <strong>#" + userId + "</strong> z systemu wraz z " +
                    "jego skrzynką pocztową."
                );
                session.getTransaction().commit();
                LOGGER.info("Employer with id: {} was succesfuly removed from system.", userId);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (Exception ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to remove employer with id: {}. Cause: {}", userId, ex.getMessage());
        }
        httpSession.setAttribute(OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/owner/employers");
    }
}
