package app.service.email;

import app.entities.Order;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;

public class EmailService {

    // Initialiser dotenv for at læse miljøvariabler
    private static final Dotenv dotenv = Dotenv.load();

    // Hent API-nøgle og afsender-email fra .env
    private static final String SENDGRID_API_KEY = dotenv.get("SENDGRID_API_KEY");
    private static final String FROM_EMAIL = dotenv.get("FROM_EMAIL");

    public static void sendOfferEmail(Order order, String customerEmail) throws IOException {
        String subject = "Tilbud på din carport";
        String content = buildOfferContent(order);

        Email from = new Email(FROM_EMAIL);
        Email to = new Email(customerEmail);
        Content emailContent = new Content("text/plain", content);
        Mail mail = new Mail(from, subject, to, emailContent);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException ex) {
            throw new IOException("Fejl ved afsendelse af email: " + ex.getMessage(), ex);
        }
    }

    // Bygger emailens indhold baseret på ordredetaljer
    private static String buildOfferContent(Order order) {
        return "Hej,\n\nVi har opdateret dit tilbud.\n\n" +
                "Bredde: " + order.getCarportWidth() + " cm\n" +
                "Længde: " + order.getCarportLength() + " cm\n" +
                "Tagtype: " + order.getRoof() + "\n" +
                "Admin kommentar: " + order.getAdminText() + "\n" +
                "Pris: " + order.getSalesPrice() + " DKK\n\n" +
                "Klik her for at betale: http://localhost:7070/pay?orderId=" + order.getOrderId() + "\n\n" +
                "Venlig hilsen,\nCarport Teamet";
    }
}
