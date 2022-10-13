package com.barrans.master.hook;

import com.barrans.master.util.EmailUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.vertx.ConsumeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EmailNotificationHook {
	Logger LOGGER = LoggerFactory.getLogger(EmailNotificationHook.class);

	ObjectMapper objMapper = new ObjectMapper();

    @Inject
    EmailUtil emailUtil;

	public static final String EVBUS_ADDR_EMAIL_SUBMIT_SUPPORT = "EVBUS_ADDR_EMAIL_SUBMIT_SUPPORT"; // Event Key

	@PostConstruct
	public void listen() {
		// register the required time module
		objMapper.registerModule(new JavaTimeModule());
	}

	@ConsumeEvent(value = EVBUS_ADDR_EMAIL_SUBMIT_SUPPORT, blocking = true)
	public void sendSubmitEmailSupport(String msg) {
		LOGGER.info("Received email submit support msg : {}", msg);

		try {
            JsonNode request = objMapper.readTree(msg);

        } catch (Exception e) {
			LOGGER.error("Failed to process hook", e);
        }
    }

}
