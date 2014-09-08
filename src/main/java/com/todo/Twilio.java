package com.todo;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Sms;

import javax.ws.rs.WebApplicationException;
import java.util.HashMap;
import java.util.Map;

class Twilio {

  private static final String ACCOUNT_SID = "ACac0b5cb930c8c535c830dea4bbd55a89";
  private static final String AUTH_TOKEN = "8bc7efc8d8714339887d612ee5374cbc";

  private String donePhone;

  Twilio() {
    donePhone = System.getProperty("donePhone");
    if (donePhone == null) {
      throw new WebApplicationException("System property \"donePhone\" must be set (for example -DdonePhone=+15105890752");
    }
  }

  void sendMessage(String message) {
    TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

    Map<String, String> params = new HashMap<>();
    params.put("To", donePhone);
    params.put("From", "+15103984486");
    params.put("Body", message);

    SmsFactory messageFactory = client.getAccount().getSmsFactory();
    Sms sms = null;
    try {
      sms = messageFactory.create(params);
    } catch (TwilioRestException e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
    System.out.println(String.format("SMS message sent with sid = %s and status = %s.", sms.getSid(), sms.getStatus()));
  }
}