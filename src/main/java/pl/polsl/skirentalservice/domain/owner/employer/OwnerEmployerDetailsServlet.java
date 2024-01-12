/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.employer;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.service.OwnerEmployerService;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/owner/employer-details")
public class OwnerEmployerDetailsServlet extends AbstractWebServlet {
    private final OwnerEmployerService ownerEmployerService;

    @Inject
    public OwnerEmployerDetailsServlet(
        OwnerEmployerService ownerEmployerService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.ownerEmployerService = ownerEmployerService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final Long employerId = req.getAttribute("employerId", Long.class);
        final AlertTupleDto alert = new AlertTupleDto(true);

        String redirectOrViewName = "owner/employers";
        HttpMethodMode mode = HttpMethodMode.REDIRECT;
        try {
            req.addAttribute("employerData", ownerEmployerService.getEmployerFullDetails(employerId));
            mode = HttpMethodMode.JSP_GENERATOR;
            redirectOrViewName = "owner/employer/owner-employer-details";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT, alert);
        }
        return WebServletResponse.builder()
            .mode(mode)
            .pageTitle(PageTitle.OWNER_EDIT_EMPLOYER_PAGE)
            .pageOrRedirectTo(redirectOrViewName)
            .build();
    }
}
