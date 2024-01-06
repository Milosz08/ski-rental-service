/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.JAXBProperty;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static pl.polsl.skirentalservice.exception.ServletException.UnableToSendEmailException;

@Slf4j
public class MailSocketSingleton {
    private static final ConfigSingleton config = ConfigSingleton.getInstance();

    private static final String MAIL_CFG = "/mail/mail.cfg.xml";
    private static final String FREEMARKER_PATH = "/mail/templates";

    private Session mailSession;
    private Configuration freemarkerConfig;
    private List<JAXBProperty> configProperties;

    private static volatile MailSocketSingleton instance;

    private MailSocketSingleton() {
        try {
            freemarkerConfig = new Configuration(Configuration.VERSION_2_3_22);
            freemarkerConfig.setClassForTemplateLoading(MailSocketSingleton.class, FREEMARKER_PATH);
            log.info("Successful loaded freemarker template engine cache path. Cache path: {}", FREEMARKER_PATH);

            final JAXBContext jaxbContext = JAXBContext.newInstance(JAXBMailConfig.class);
            final var config = (JAXBMailConfig) jaxbContext.createUnmarshaller()
                .unmarshal(MailSocketSingleton.class.getResource(MAIL_CFG));
            configProperties = config.getProperties();

            final Properties properties = new Properties();
            final List<JAXBProperty> withoutCredentials = config.getProperties().stream()
                .filter(p -> !p.getName().equals("mail.smtp.user") && !p.getName().equals("mail.smtp.pass"))
                .toList();

            for (final JAXBProperty property : withoutCredentials) {
                properties.put(property.getName(), property.getValue());
            }
            final Authenticator authenticator = new JakartaMailAuthenticator(config.getProperties());
            mailSession = Session.getInstance(properties, authenticator);
            log.info("Successful loaded JavaMail API properties with authentication. Props: {}", properties);
        } catch (JAXBException ex) {
            log.error("Unable to load mail properties from extended XML file: {}", MAIL_CFG);
        }
    }

    public void sendMessage(String sendTo, MailRequestPayload payload, HttpServletRequest req) {
        sendMessage(List.of(sendTo), payload, req);
    }

    public void sendMessage(List<String> sendTo, MailRequestPayload payload, HttpServletRequest req) {
        try {
            final Message message = new MimeMessage(mailSession);
            final Template bodyTemplate = freemarkerConfig.getTemplate(payload.getTemplateName());
            final Writer outWriter = new StringWriter();

            final Map<String, Object> addtlnPayloadProps = new HashMap<>(payload.getTemplateVars());
            addtlnPayloadProps.put("messageResponder", payload.getMessageResponder());
            addtlnPayloadProps.put("serverUtcTime", Instant.now().toString());
            addtlnPayloadProps.put("baseServletPath", getBaseReqPath(req));
            addtlnPayloadProps.put("currentYear", String.valueOf(LocalDate.now().getYear()));
            addtlnPayloadProps.put("systemVersion", config.getSystemVersion());
            bodyTemplate.process(addtlnPayloadProps, outWriter);

            final Address[] sendToAddresses = new Address[sendTo.size()];
            for (int i = 0; i < sendToAddresses.length; i++) {
                sendToAddresses[i] = new InternetAddress(sendTo.get(i));
            }
            message.setFrom(new InternetAddress(JakartaMailAuthenticator.findProperty(configProperties, "mail.smtp.user"),
                config.getDefPageTitle()));
            message.setRecipients(Message.RecipientType.TO, sendToAddresses);

            if (!Objects.isNull(payload.getAttachmentsPaths())) {
                final Multipart multipart = new MimeMultipart();
                final BodyPart bodyPart = new MimeBodyPart();

                bodyPart.setContent(outWriter.toString(), "text/html;charset=UTF-8");
                multipart.addBodyPart(bodyPart);

                final MimeBodyPart attachementsPart = new MimeBodyPart();
                for (String filePath : payload.getAttachmentsPaths()) {
                    attachementsPart.attachFile(new File(filePath));
                }
                multipart.addBodyPart(attachementsPart);
                message.setContent(multipart);
            } else {
                message.setContent(outWriter.toString(), "text/html;charset=UTF-8");
            }
            message.setSubject(payload.getSubject());
            message.setSentDate(new Date());

            Transport.send(message);
            log.info("Successful send email message to the following recipent/s: {}", sendTo);
        } catch (IOException ex) {
            log.error("Unable to load freemarker template. Template name: {}", payload.getTemplateName());
            throw new UnableToSendEmailException(String.join(", ", sendTo), payload);
        } catch (TemplateException ex) {
            log.error("Unable to process freemarker template. Exception: {}", ex.getMessage());
            throw new UnableToSendEmailException(String.join(", ", sendTo), payload);
        } catch (MessagingException | RuntimeException ex) {
            throw new UnableToSendEmailException(String.join(", ", sendTo), payload);
        }
    }

    public String getDomain() {
        return "@" + configProperties.stream()
            .filter(p -> p.getName().equals("mail.smtp.domain"))
            .findFirst().map(JAXBProperty::getValue).orElse("localhost");
    }

    private String getBaseReqPath(HttpServletRequest req) {
        final boolean isHttp = req.getScheme().equals("http") && req.getServerPort() == 80;
        final boolean isHttps = req.getScheme().equals("https") && req.getServerPort() == 443;
        return req.getScheme() + "://" + req.getServerName() + (isHttp || isHttps ? "" : ":" + req.getServerPort());
    }

    public static synchronized MailSocketSingleton getInstance() {
        if (Objects.isNull(instance)) {
            instance = new MailSocketSingleton();
        }
        return instance;
    }
}
