package com.barrans.master.util;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

@ApplicationScoped
public class EmailUtil {

	Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);

    ObjectMapper objMapper = new ObjectMapper();

	@Inject
	@Channel("send-email-out")
	Emitter<String> emitterSendEmail;

	@Inject
	@Channel("send-templated-email-out")
	Emitter<String> emitterSendTemplatedEmail;

	public static final String RISK_NIL_REPORT_NEED_APPROVAL = "RISK_NIL_REPORT_NEED_APPROVAL";
	public static final String RISK_REGISTER_SUMMARY = "RISK_REGISTER_SUMMARY";

	@PostConstruct
    public void listen() {
        // register the required time module
        objMapper.registerModule(new JavaTimeModule());
    }

	public void send(String to, String subject, String body) {

		ObjectNode msgNode = JsonNodeFactory.instance.objectNode();

		msgNode.put("to", to);
		msgNode.put("subject", subject);
		msgNode.put("text", body);

		LOGGER.info("send() msg: {}", msgNode);
		emitterSendEmail.send(msgNode.toString());
	}

	public void sendTemplatedEmail(String templateName, String to, ObjectNode parameter) {
		// construct templated email message
		ObjectNode msgNode = JsonNodeFactory.instance.objectNode();

		msgNode.put("to", to);
		msgNode.put("template", templateName);

        Iterator<String> iterDataNode = parameter.fieldNames();
        while (iterDataNode.hasNext()) {
            String keyName = iterDataNode.next();
            msgNode.set(keyName, parameter.get(keyName));
        }

		LOGGER.info("sendTemplatedEmail() msg: {}", msgNode);
		emitterSendTemplatedEmail.send(msgNode.toString());
	}

	public void sendNilReportNeedApproval(String to, String pincabName, String createdDate, ArrayNode reportList) {
		ObjectNode param = JsonNodeFactory.instance.objectNode();
		param.put("pincabName", pincabName);
		param.put("firstDate", createdDate);
		param.set("reportList", reportList);  // [{ "regionalName": "Jawa Barat2", "branchName": "Bandung" }]

		sendTemplatedEmail(RISK_NIL_REPORT_NEED_APPROVAL, to, param);
	}

	public void sendReportSummary(String to, ArrayNode reportList) {
		ObjectNode param = JsonNodeFactory.instance.objectNode();
		param.set("reportList", reportList);

		sendTemplatedEmail(RISK_REGISTER_SUMMARY, to, param);
	}

}
