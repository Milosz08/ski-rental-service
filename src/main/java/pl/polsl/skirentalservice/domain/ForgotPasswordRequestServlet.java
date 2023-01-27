/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ForgotPasswordRequestServlet.java
 *  Last modified: 31/12/2022, 17:16
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.util.*;
import java.text.*;
import java.io.IOException;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.core.mail.*;
import pl.polsl.skirentalservice.dto.change_password.*;
import pl.polsl.skirentalservice.core.db.HibernateBean;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.PageTitle.FORGOT_PASSWORD_REQUEST_PAGE;
import static pl.polsl.skirentalservice.util.SessionAlert.FORGOT_PASSWORD_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/forgot-password-request")
public class ForgotPasswordRequestServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordRequestServlet.class);
    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd, kk:mm:ss", new Locale("pl"));

    @EJB private HibernateBean database;
    @EJB private ValidatorBean validator;
    @EJB private MailSocketBean mailSocket;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("alertData", getAndDestroySessionAlert(req, FORGOT_PASSWORD_PAGE_ALERT));
        req.setAttribute("resetPassData", getFromSessionAndDestroy(req, getClass().getName(),
            RequestToChangePasswordResDto.class));
        req.setAttribute("title", FORGOT_PASSWORD_REQUEST_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/forgot-password-request.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final RequestToChangePasswordReqDto reqDto = new RequestToChangePasswordReqDto(req);
        final RequestToChangePasswordResDto resDto = new RequestToChangePasswordResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/forgot-password-request");
            return;
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final String jpqlFindEmployer =
                    "SELECT COUNT(*) > 0 FROM EmployerEntity e " +
                    "INNER JOIN e.userDetails d " +
                    "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
                final boolean employerExist = session.createQuery(jpqlFindEmployer, Boolean.class)
                    .setParameter("loginOrEmail", reqDto.getLoginOrEmail())
                    .getSingleResult();
                if (!employerExist) throw new UserNotFoundException(reqDto, LOGGER);

                final String jpqlEmployerDetails =
                    "SELECT new pl.polsl.skirentalservice.dto.change_password.EmployerDetailsDto(" +
                        "e.id, e.login, CONCAT(d.firstName, ' ', d.lastName), d.emailAddress " +
                        ") FROM EmployerEntity e " +
                    "INNER JOIN e.userDetails d " +
                    "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
                final var employer = session.createQuery(jpqlEmployerDetails, EmployerDetailsDto.class)
                    .setParameter("loginOrEmail", reqDto.getLoginOrEmail())
                    .getSingleResultOrNull();

                final String token = randomAlphanumeric(10);
                final EmployerEntity employerEntity = session.getReference(EmployerEntity.class, employer.getId());
                final OtaTokenEntity otaToken = new OtaTokenEntity(token, employerEntity);
                session.persist(otaToken);

                final Map<String, Object> templateVars = new HashMap<>();
                templateVars.put("token", token);

                final MailRequestPayload payload = MailRequestPayload.builder()
                    .messageResponder(employer.getFullName())
                    .subject("SkiRent Service | Zmiana hasła dla użytkownika " + employer.getFullName())
                    .templateName("change-password.template.ftl")
                    .templateVars(templateVars)
                    .build();

                mailSocket.sendMessage(employer.getEmailAddress(), payload, req);
                alert.setType(INFO);
                alert.setMessage(
                    "Na adres email <strong>" + employer.getEmailAddress() + "</strong> został przesłany link aktywacyjny."
                );
                resDto.getLoginOrEmail().setValue(EMPTY);
                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
        }
        httpSession.setAttribute(FORGOT_PASSWORD_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/forgot-password-request");
    }
}
