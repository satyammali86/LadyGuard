package com.example.women_safety;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private Session mSession;

    private String mEmail;
    private String mSubject;
    private String mMessage;

    // NOTE: Do not hardcode credentials in production.
    // Consider retrieving them from a secure server or using a backend service.
    private static final String SENDER_EMAIL = "womensafety06@gmail.com";     // Replace with your email
    private static final String SENDER_PASSWORD = "idzd cfzd vvjh dzuk";           // Replace with your email password

    public JavaMailAPI(Context context, String email, String subject, String message) {
        mContext = context;
        mEmail = email;
        mSubject = subject;
        mMessage = message;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties props = new Properties();
        // Configuration for Gmail SMTP
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        mSession = Session.getInstance(props, new javax.mail.Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            MimeMessage mm = new MimeMessage(mSession);
            mm.setFrom(new InternetAddress(SENDER_EMAIL));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(mEmail));
            mm.setSubject(mSubject);
            mm.setText(mMessage);

            Transport.send(mm);
            Log.d("JavaMailAPI", "Email sent successfully.");

        } catch (MessagingException e) {
            Log.e("JavaMailAPI", "Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
