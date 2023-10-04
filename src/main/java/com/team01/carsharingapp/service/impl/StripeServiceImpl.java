package com.team01.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.team01.carsharingapp.dto.stripe.StripeDto;
import com.team01.carsharingapp.exception.PaymentException;
import com.team01.carsharingapp.model.Payment;
import com.team01.carsharingapp.service.StripeService;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements StripeService {
    private static final int CONVERT_CENT = 100;
    private static final Long MAX_QUANTITY = 1L;
    private static final Long EXPIRATION_TIME = Instant.now().getEpochSecond() + 86400L;
    private static final String URL_API = "http://localhost:8080/api/payment";
    private static final String SUCCESS_ENDPOINT = "/success";
    private static final String CANCEL_ENDPOINT = "/cancel";
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Override
    public StripeDto pay(Payment payment,
                         String currency) {
        Stripe.apiKey = stripeSecretKey;
        Long totalAmount = payment.getPrice()
                .multiply(BigDecimal.valueOf(CONVERT_CENT)).longValue();
        Product product = createProduct(payment.getRental().getCar().getModel());
        Price price = createPrice(product, totalAmount, currency);
        Session session = createSession(price);
        StripeDto stripeDto = new StripeDto();
        stripeDto.setSessionId(session.getId());
        stripeDto.setSessionUrl(session.getUrl());
        return stripeDto;
    }

    private Price createPrice(Product product, Long totalAmount, String currency) {
        PriceCreateParams priceParams = PriceCreateParams.builder()
                .setCurrency(currency)
                .setProduct(product.getId())
                .setUnitAmount(totalAmount)
                .build();
        Price price;
        try {
            price = Price.create(priceParams);
        } catch (StripeException e) {
            throw new PaymentException("Can't create price model for pay."
                    + "Product id: "
                    + product.getId(), e);
        }
        return price;
    }

    private Product createProduct(String productName) {
        ProductCreateParams productParams = new ProductCreateParams.Builder()
                .setName(productName)
                .build();
        Product product;
        try {
            product = Product.create(productParams);
        } catch (StripeException e) {
            throw new PaymentException("Can't create product with product name: "
                    + productName, e);
        }
        return product;
    }

    private Session createSession(Price price) {
        SessionCreateParams sessionCreateParams = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(price.getId())
                        .setQuantity(MAX_QUANTITY)
                        .build())
                .setExpiresAt(EXPIRATION_TIME)
                .setSuccessUrl(URL_API
                        + SUCCESS_ENDPOINT
                        + "?sessionId={CHECKOUT_SESSION_ID}")
                .setCancelUrl(URL_API + CANCEL_ENDPOINT)
                .build();
        Session session;
        try {
            session = Session.create(sessionCreateParams);
        } catch (StripeException e) {
            throw new PaymentException("Can't create session with Price id: "
                    + price.getId(), e);
        }
        return session;
    }
}
