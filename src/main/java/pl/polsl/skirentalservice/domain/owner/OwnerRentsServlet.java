/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: OwnerRentsServlet.java
 * Last modified: 6/3/23, 12:42 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
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
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.rent.OwnerRentRecordResDto;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.rent.RentDao;
import pl.polsl.skirentalservice.dao.rent.IRentDao;
import pl.polsl.skirentalservice.paging.filter.FilterColumn;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.paging.filter.ServletFilter;
import pl.polsl.skirentalservice.paging.sorter.ServletSorter;
import pl.polsl.skirentalservice.paging.sorter.SorterDataDto;
import pl.polsl.skirentalservice.paging.sorter.ServletSorterField;
import pl.polsl.skirentalservice.paging.pagination.ServletPagination;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/rents")
public class OwnerRentsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerRentsServlet.class);
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    private final Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private final List<FilterColumn> filterFieldMap = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void init() {
        sorterFieldMap.put("identity", new ServletSorterField("r.id"));
        sorterFieldMap.put("issuedIdentifier", new ServletSorterField("r.issuedIdentifier"));
        sorterFieldMap.put("issuedDateTime", new ServletSorterField("r.issuedDateTime"));
        sorterFieldMap.put("status", new ServletSorterField("r.status"));
        sorterFieldMap.put("totalPriceNetto", new ServletSorterField("r.totalPrice"));
        sorterFieldMap.put("totalPriceBrutto", new ServletSorterField("(r.tax / 100) * r.totalPrice + r.totalPrice"));
        sorterFieldMap.put("client", new ServletSorterField("CONCAT(d.firstName, ' ', d.lastName)"));
        sorterFieldMap.put("employer", new ServletSorterField("CONCAT(ed.firstName, ' ', ed.lastName)"));
        filterFieldMap.add(new FilterColumn("issuedIdentifier", "Numerze wypożyczenia", "r.issuedIdentifier"));
        filterFieldMap.add(new FilterColumn("issuedDateTime", "Dacie stworzenia wypożyczenia", "CAST(r.issuedDateTime AS string)"));
        filterFieldMap.add(new FilterColumn("status", "Statusie wypożyczenia", "CAST(r.status AS string)"));
        filterFieldMap.add(new FilterColumn("client", "Po imieniu i nazwisku klienta", "CONCAT(d.firstName, ' ', d.lastName)"));
        filterFieldMap.add(new FilterColumn("employer", "Po imieniu i nazwisku pracownika", "CONCAT(ed.firstName, ' ', ed.lastName)"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("total"), "10"), 10);

        final ServletSorter servletSorter = new ServletSorter(req, "r.id", sorterFieldMap);
        final SorterDataDto sorterData = servletSorter.generateSortingJPQuery(SessionAttribute.RENTS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        final FilterDataDto filterData = servletFilter.generateFilterJPQuery(SessionAttribute.RENTS_LIST_FILTER);

        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.COMMON_RENTS_PAGE_ALERT);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final IRentDao rentDao = new RentDao(session);

                final Long totalRents = rentDao.findAllRentsCount(filterData);
                final ServletPagination pagination = new ServletPagination(page, total, totalRents);
                if (pagination.checkIfIsInvalid()) throw new RuntimeException();

                final List<OwnerRentRecordResDto> rentsList = rentDao
                    .findAllPageableRents(new PageableDto(filterData, sorterData, page, total));

                session.getTransaction().commit();
                req.setAttribute("pagesData", pagination);
                req.setAttribute("rentsData", rentsList);
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
        req.setAttribute("title", PageTitle.COMMON_RENTS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/rent/owner-rents.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("total"), "10"), 10);
        final ServletSorter servletSorter = new ServletSorter(req, "r.id", sorterFieldMap);
        servletSorter.generateSortingJPQuery(SessionAttribute.RENTS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        servletFilter.generateFilterJPQuery(SessionAttribute.RENTS_LIST_FILTER);
        res.sendRedirect("/owner/rents?page=" + page + "&total=" + total);
    }
}
