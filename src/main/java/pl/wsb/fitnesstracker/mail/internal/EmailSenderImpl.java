/*
 ____            _            _____ _             _     _      ___ ___ ___ ___ ___
|    \ ___ _____|_|___ ___   |   __| |_ ___ ___  |_|___| |_   | . | . | . |_  |  _|
|  |  | .'|     | | .'|   |  |__   |  _|  _| . | | | -_| '_|  |_  |_  | . |_  | . |
|____/|__,|_|_|_|_|__,|_|_|  |_____|_| |_| |___|_| |___|_,_|  |___|___|___|___|___|
                                               |___|
 */
package pl.wsb.fitnesstracker.mail.internal;

import pl.wsb.fitnesstracker.mail.api.EmailDto;
import pl.wsb.fitnesstracker.mail.api.EmailSender;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link EmailSender} interface.
 */
@Service
public class EmailSenderImpl implements EmailSender {

    /**
     * JavaMailSender instance used to send emails.
     */
    private final JavaMailSender javaMailSender;

    public EmailSenderImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Sends the email message to the recipient from the provided {@link EmailDto}.
     *
     * @param email information on email to be sent
     */
    @Override
    public void send(EmailDto email) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email.toAddress());
        message.setSubject(email.subject());
        message.setText(email.content());

        javaMailSender.send(message);
    }
}