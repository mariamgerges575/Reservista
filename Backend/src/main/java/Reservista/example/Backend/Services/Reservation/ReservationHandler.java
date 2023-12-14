package Reservista.example.Backend.Services.Reservation;

import Reservista.example.Backend.DTOs.Reservation.ReservationDTO;
import Reservista.example.Backend.DTOs.Response.ReservationResponseDTO;
import Reservista.example.Backend.DTOs.Response.ResponseDTO;
import com.stripe.exception.StripeException;

public abstract class ReservationHandler {
    protected ReservationHandler nextHandler;

    public abstract ResponseDTO<ReservationResponseDTO> handleRequest(ReservationDTO reservationDTO) throws StripeException;
    public void setNextHandler(ReservationHandler reservationHandler){
        if(reservationHandler != null)
            this.nextHandler = reservationHandler;
    }
}
