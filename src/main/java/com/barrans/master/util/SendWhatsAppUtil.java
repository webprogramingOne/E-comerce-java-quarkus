package com.barrans.master.util;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SendWhatsAppUtil {

	Logger log = LoggerFactory.getLogger(SendWhatsAppUtil.class);

    @Inject
    @Channel("send-whatsapp")
    Emitter<String> sendWhatsappEmmiter;

	public void send(String recipient, String message) {
		ObjectNode msgNode = JsonNodeFactory.instance.objectNode();

		msgNode.put("recipient", recipient);
		msgNode.put("message", message);

		log.info("Sending to kafka whatsapp: {}", msgNode.toString());
		sendWhatsappEmmiter.send(msgNode.toString());
	}

}
