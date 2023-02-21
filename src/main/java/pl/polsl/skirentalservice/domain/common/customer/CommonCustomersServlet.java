/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: CommonCustomersServlet.java
 *  Last modified: 09/02/2023, 01:07
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.common.customer;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.dao.customer.*;
import pl.polsl.skirentalservice.paging.filter.*;
import pl.polsl.skirentalservice.paging.sorter.*;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.customer.CustomerRecordResDto;
import pl.polsl.skirentalservice.paging.pagination.ServletPagination;

import java.util.*;
import java.io.IOException;

import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import static pl.polsl.skirentalservice.util.AlertType.ERROR;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;
import static pl.polsl.skirentalservice.util.PageTitle.COMMON_CUSTOMERS_PAGE;
import static pl.polsl.skirentalservice.util.Utils.getAndDestroySessionAlert;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet(urlPatterns = { "/seller/customers", "/owner/customers" })
public class CommonCustomersServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonCustomersServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    private final Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private final List<FilterColumn> filterFieldMap = new ArrayList<>();

    private final String addressColumn =
        "CONCAT('ul. ', a.street, ' ', a.buildingNr, IF(a.apartmentNr, CONCAT('/', a.apartmentNr), '')," +
        "', ', a.postalCode, ' ', a.city)";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void init() {
        sorterFieldMap.put("identity", new ServletSorterField("c.id"));
        sorterFieldMap.put("fullName", new ServletSorterField("CONCAT(d.firstName, ' ', d.lastName)"));
        sorterFieldMap.put("pesel", new ServletSorterField("d.pesel"));
        sorterFieldMap.put("email", new ServletSorterField("d.emailAddress"));
        sorterFieldMap.put("phoneNumber", new ServletSorterField("CONCAT('+', d.phoneAreaCode, ' ', d.phoneNumber)"));
        sorterFieldMap.put("address", new ServletSorterField(addressColumn));
        filterFieldMap.add(new FilterColumn("fullName", "Imieniu i nazwisku", "CONCAT(d.firstName, ' ', d.lastName)"));
        filterFieldMap.add(new FilterColumn("pesel", "Numerze PESEL", "d.pesel"));
        filterFieldMap.add(new FilterColumn("email", "Adresie email", "d.emailAddress"));
        filterFieldMap.add(new FilterColumn("phoneNumber", "Numerze telefonu", "d.phoneNumber"));
        filterFieldMap.add(new FilterColumn("address", "Adresie zamieszkania", addressColumn));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = toInt(requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = toInt(requireNonNullElse(req.getParameter("total"), "10"), 10);

        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        final ServletSorter servletSorter = new ServletSorter(req, "c.id", sorterFieldMap);
        final SorterDataDto sorterData = servletSorter.generateSortingJPQuery(CUSTOMERS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        final FilterDataDto filterData = servletFilter.generateFilterJPQuery(CUSTOMERS_LIST_FILTER);

        final AlertTupleDto alert = getAndDestroySessionAlert(req, COMMON_CUSTOMERS_PAGE_ALERT);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final ICustomerDao customerDao = new CustomerDao(session);

                final Long totalCustomers = customerDao.findAllCustomersCount(filterData);
                final ServletPagination pagination = new ServletPagination(page, total, totalCustomers);
                if (pagination.checkIfIsInvalid()) throw new RuntimeException();

                final List<CustomerRecordResDto> customersList = customerDao
                    .findAllPageableCustomers(new PageableDto(filterData, sorterData, page, total), addressColumn);

                session.getTransaction().commit();
                req.setAttribute("pagesData", pagination);
                req.setAttribute("customersData", customersList);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setType(ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("sorterData", sorterFieldMap);
        req.setAttribute("filterData", filterData);
        req.setAttribute("title", COMMON_CUSTOMERS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/customer/" +
            userDataDto.getRoleEng() + "-customers.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = toInt(requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = toInt(requireNonNullElse(req.getParameter("total"), "10"), 10);

        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        final ServletSorter servletSorter = new ServletSorter(req, "c.id", sorterFieldMap);
        servletSorter.generateSortingJPQuery(CUSTOMERS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        servletFilter.generateFilterJPQuery(CUSTOMERS_LIST_FILTER);

        res.sendRedirect("/" + userDataDto.getRoleEng() + "/customers?page=" + page + "&total=" + total);
    }
}
