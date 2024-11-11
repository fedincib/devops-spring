package com.example.pifinity.serviceImpl;

import com.example.pifinity.serviceInterface.ISms;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;


import com.twilio.Twilio;
import com.twilio.converter.Promoter;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.net.URI;
import java.math.BigDecimal;


@Service
public class SmsServiceImpl implements ISms {

    public static final String ACCOUNT_SID = "AC9e9d8e3b83c2ab491b9d7201d877f16c";
    public static final String AUTH_TOKEN = "a3f4c40f8ddd04338b4433cbaf9b713b";
    public static final String OUTGOING_SMS_NUMBER = "+16204728193";




    @PostConstruct
    private void setup() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    @Override
    public String sendSms(String smsNumber, String smsMessage, int randomInteger) {
        Message message = Message.creator(
                        new PhoneNumber(smsNumber),
                        new PhoneNumber(OUTGOING_SMS_NUMBER),
                        smsMessage + " Random Integer: " + randomInteger)
                .create();

        return message.getStatus().toString();
    }


}
