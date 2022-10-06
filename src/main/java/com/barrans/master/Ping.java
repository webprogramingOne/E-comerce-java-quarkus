package com.barrans.master;

import com.barrans.master.util.InternalDateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class Ping {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	public Date dateTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	public LocalDateTime localDateTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	public OffsetDateTime offsetDateTime;

	public String systemZoneId;
	public String offsetId;
	public String localeId;
	public String jakartaDateTime;

	public Ping() {
		this.dateTime = new Date();
		this.localDateTime = LocalDateTime.now();
		this.offsetDateTime = OffsetDateTime.now();
		this.systemZoneId = ZoneId.systemDefault().getId();
		this.localeId = Locale.getDefault().getDisplayName();
        this.offsetId = this.offsetDateTime.getOffset().getId();
        this.jakartaDateTime = InternalDateUtil.convertLocalDateTimeToJakartaString(this.localDateTime, "dd MMMM YYYY HH:mm:ss");
	}
}
