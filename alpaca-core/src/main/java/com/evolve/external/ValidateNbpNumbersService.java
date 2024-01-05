package com.evolve.external;

import com.evolve.alpaca.utils.LogUtil;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Optional;

@Service
@Slf4j
public class ValidateNbpNumbersService {
    private final String nbpUrl;

    public ValidateNbpNumbersService(@Value("${alpaca.nbp.url}") String nbpUrl) {
        this.nbpUrl = nbpUrl;
    }

    @Cacheable(value="bankDetails", key="#number")
    public Optional<BankDetails> getBankDetails(String number) {
        final String url = String.format(nbpUrl, number);
        //final WebClient webClient = WebClient.create();
        try {
            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            HttpClient httpConnector = HttpClient.create()
                    .secure(t -> t.sslContext(sslContext) );

            final WebClient webClient = WebClient.builder().clientConnector(
                     new ReactorClientHttpConnector(httpConnector)).build();

            final BankDetails bankDetails = webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(response -> {
                        if (response.statusCode().equals(HttpStatus.OK)) {
                            return response.bodyToMono(BankDetails.class);
                        } else {
                            // Turn to error
                            return response.createException().flatMap(Mono::error);
                        }
                    })
                    .block();
            log.info("Returned bank details: {}", LogUtil.printJson(bankDetails));
            return Optional.ofNullable(bankDetails);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

}
