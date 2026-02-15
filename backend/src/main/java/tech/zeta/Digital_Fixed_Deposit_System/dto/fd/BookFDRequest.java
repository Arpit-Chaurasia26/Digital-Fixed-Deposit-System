package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;


import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


import java.math.BigDecimal;

/**
 * @author Yogendra Kavuru
 */
public class BookFDRequest {


    @NotNull(message = "FD amount is required")
    @Positive(message = "FD amount must be greater than zero")
    private BigDecimal amount;


    @NotNull(message = "Interest scheme must be selected")
    private InterestScheme interestScheme;


    public BigDecimal getAmount() {
        return amount;
    }


    public InterestScheme getInterestScheme() {
        return interestScheme;
    }
}

