/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.customer.CustomerDao;
import pl.polsl.skirentalservice.dao.customer.ICustomerDao;
import pl.polsl.skirentalservice.dao.equipment.EquipmentDao;
import pl.polsl.skirentalservice.dao.equipment.IEquipmentDao;
import pl.polsl.skirentalservice.dao.rent.IRentDao;
import pl.polsl.skirentalservice.dao.rent.RentDao;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.entity.CustomerEntity;
import pl.polsl.skirentalservice.entity.RentEntity;
import pl.polsl.skirentalservice.entity.RentEquipmentEntity;
import pl.polsl.skirentalservice.util.*;

import java.io.IOException;
import java.util.Objects;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.CustomerHasOpenedRentsException;
import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;

@WebServlet("/seller/delete-customer")
public class SellerDeleteCustomerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerDeleteCustomerServlet.class);
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final Long userId = NumberUtils.toLong(req.getParameter("id"));
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final IEquipmentDao equipmentDao = new EquipmentDao(session);
                final ICustomerDao customerDao = new CustomerDao(session);
                final IRentDao rentDao = new RentDao(session);

                final CustomerEntity customerEntity = session.get(CustomerEntity.class, userId);
                if (Objects.isNull(customerEntity)) throw new UserNotFoundException(UserRole.SELLER);
                if (customerDao.checkIfCustomerHasAnyActiveRents(userId)) {
                    throw new CustomerHasOpenedRentsException();
                }
                for (final RentEntity rentEntity : rentDao.findAllRentsBaseCustomerId(userId)) {
                    for (final RentEquipmentEntity equipment : rentEntity.getEquipments()) {
                        if (Objects.isNull(equipment.getEquipment())) continue;
                        equipmentDao.increaseAvailableSelectedEquipmentCount(equipment.getEquipment().getId(),
                            equipment.getCount());
                    }
                }
                final var rentData = (InMemoryRentDataDto) httpSession
                    .getAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
                if (!Objects.isNull(rentData) && rentData.getCustomerId().equals(userId)) {
                    httpSession.removeAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
                }
                session.remove(customerEntity);
                session.getTransaction().commit();
                alert.setType(AlertType.INFO);
                alert.setMessage("Pomyślnie usunięto klienta z ID <strong>#" + userId + "</strong> z systemu.");
                LOGGER.info("Customer with id: {} was succesfuly removed from system.", userId);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to remove customer with id: {}. Cause: {}", userId, ex.getMessage());
        }
        httpSession.setAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/customers");
    }
}
