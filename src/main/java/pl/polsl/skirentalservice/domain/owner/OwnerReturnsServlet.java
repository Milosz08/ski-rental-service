/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerReturnsServlet.java
 *  Last modified: 31/01/2023, 03:36
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.*;
import java.io.IOException;

import org.apache.commons.lang3.math.NumberUtils;

import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto;
import pl.polsl.skirentalservice.dao.return_deliv.ReturnDao;
import pl.polsl.skirentalservice.dao.return_deliv.IReturnDao;
import pl.polsl.skirentalservice.paging.filter.FilterColumn;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.paging.filter.ServletFilter;
import pl.polsl.skirentalservice.paging.sorter.ServletSorter;
import pl.polsl.skirentalservice.paging.sorter.SorterDataDto;
import pl.polsl.skirentalservice.paging.sorter.ServletSorterField;
import pl.polsl.skirentalservice.paging.pagination.ServletPagination;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/returns")
public class OwnerReturnsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerReturnsServlet.class);
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    private final Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private final List<FilterColumn> filterFieldMap = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void init() {
        sorterFieldMap.put("identity", new ServletSorterField("r.id"));
        sorterFieldMap.put("issuedIdentifier", new ServletSorterField("r.issuedIdentifier"));
        sorterFieldMap.put("issuedDateTime", new ServletSorterField("r.issuedDateTime"));
        sorterFieldMap.put("totalPriceNetto", new ServletSorterField("r.totalPrice"));
        sorterFieldMap.put("totalPriceBrutto", new ServletSorterField("(rd.tax / 100) * r.totalPrice + r.totalPrice"));
        sorterFieldMap.put("employer", new ServletSorterField("CONCAT(ed.firstName, ' ', ed.lastName)"));
        sorterFieldMap.put("rentIssuedIdentifier", new ServletSorterField("rd.issuedIdentifier"));
        filterFieldMap.add(new FilterColumn("issuedIdentifier", "Numerze zwrotu", "r.issuedIdentifier"));
        filterFieldMap.add(new FilterColumn("issuedDateTime", "Dacie stworzenia zwrotu", "CAST(r.issuedDateTime AS string)"));
        filterFieldMap.add(new FilterColumn("rentIssuedIdentifier", "Numerze wypożyczenia", "rd.issuedIdentifier"));
        filterFieldMap.add(new FilterColumn("employer", "Po imieniu i nazwisku pracownika", "CONCAT(ed.firstName, ' ', ed.lastName)"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("total"), "10"), 10);

        final ServletSorter servletSorter = new ServletSorter(req, "r.id", sorterFieldMap);
        final SorterDataDto sorterData = servletSorter.generateSortingJPQuery(SessionAttribute.RETURNS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        final FilterDataDto filterData = servletFilter.generateFilterJPQuery(SessionAttribute.RETURNS_LIST_FILTER);

        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.COMMON_RETURNS_PAGE_ALERT);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final IReturnDao returnDao = new ReturnDao(session);

                final Long totalReturns = returnDao.findAllReturnsCount(filterData);
                final ServletPagination pagination = new ServletPagination(page, total, totalReturns);
                if (pagination.checkIfIsInvalid()) throw new RuntimeException();

                final List<OwnerRentReturnRecordResDto> returnsList = returnDao
                    .findAllPageableReturnsRecords(new PageableDto(filterData, sorterData, page, total));

                session.getTransaction().commit();
                req.setAttribute("pagesData", pagination);
                req.setAttribute("returnsData", returnsList);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setType(AlertType.ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("sorterData", sorterFieldMap);
        req.setAttribute("filterData", filterData);
        req.setAttribute("title", PageTitle.COMMON_RETURNS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/deliv_return/owner-returns.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("total"), "10"), 10);

        final ServletSorter servletSorter = new ServletSorter(req, "r.id", sorterFieldMap);
        servletSorter.generateSortingJPQuery(SessionAttribute.RETURNS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        servletFilter.generateFilterJPQuery(SessionAttribute.RETURNS_LIST_FILTER);

        res.sendRedirect("/owner/returns?page=" + page + "&total=" + total);
    }
}
