package Reservista.example.Backend.Controllers;
import Reservista.example.Backend.Services.Payment.PaymentConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class StripeWebhook {

    @Autowired
    PaymentConfirmationService paymentConfirmationService;

    @Value("${stripe.WebhookSecret}")
    private String stripeWebhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        System.out.println("webhook");
        try {


            Event event = Webhook.constructEvent(payload, sigHeader, "whsec_18c2acc6762de9e1f49ad87a17eb485318551f4a01894231ab2c2f2ff9ae4bd6");

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    System.out.println("yesssssss");
                    paymentConfirmationService.confirmReservation(event);
                    break;
                default:
                    System.out.println("another event");
                    break;
            }
            return ResponseEntity.status(HttpStatus.OK).body("Received successfully.");

        }
        catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signature verification failed.");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error handling webhook.");
        }
    }

}
