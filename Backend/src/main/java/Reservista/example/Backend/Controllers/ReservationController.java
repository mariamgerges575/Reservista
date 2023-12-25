package Reservista.example.Backend.Controllers;
import Reservista.example.Backend.DTOs.Reservation.ReservationDTO;
import Reservista.example.Backend.DTOs.Response.ReservationResponseDTO;
import Reservista.example.Backend.DTOs.Response.ResponseDTO;
import Reservista.example.Backend.Services.Reservation.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    @PostMapping("/apply_voucher")
    public ResponseDTO<Integer> applyVoucher(@AuthenticationPrincipal String username, @RequestBody String voucherCode){
        System.out.println(voucherCode);
        return reservationService.applyVoucher(username, voucherCode);
    }

    @PostMapping("/reserve")
    public ResponseDTO<ReservationResponseDTO> reserve(@AuthenticationPrincipal String username, @Valid @RequestBody ReservationDTO reservationDTO) {
       return reservationService.reserve(username,reservationDTO);
    }

    @PostMapping("/rollback")
    public void rollback(@RequestParam long reservationId) {
        reservationService.rollbackReservation(reservationId);
    }

}