package com.lulobank.clients.starter.v3.adapters.in.sqs.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lulobank.clients.starter.v3.adapters.in.sqs.event.ActivateBlacklistedClient;
import com.lulobank.clients.starter.v3.adapters.in.sqs.event.ActivateSelfCertifiedPEPClient;
import com.lulobank.clients.starter.v3.adapters.in.sqs.event.CreatePreApprovedOfferMessage;
import io.vavr.control.Try;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class EntityFactory {


    public static class ActivateBlacklistedClientFactory {

        public static ActivateBlacklistedClient getActivateBlacklistedClient() {
            URL url = Resources.getResource("mocks/blacklist/ActivateBlacklistedClientEvent.json");
            String contentFile = Try.of(() -> new BufferedReader(new FileReader(url.getPath())))
                    .map(br -> br.lines().collect(Collectors.joining()))
                    .get();

            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new Util.JsonDateTimeArrayDeserializer()).create();

            return gson.fromJson(contentFile, ActivateBlacklistedClient.class);
        }

    }

    public static class ActivateSelfCertifiedPEPClientFactory {

        public static ActivateSelfCertifiedPEPClient getActivateSelfCertifiedPEPClient() throws JsonProcessingException {
            URL url = Resources.getResource("mocks/pep-reactivation/ActivateSelfCertifiedPEPClientEvent.json");
            String contentFile = Try.of(() -> new BufferedReader(new FileReader(url.getPath())))
                    .map(br -> br.lines().collect(Collectors.joining()))
                    .get();

            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(contentFile, ActivateSelfCertifiedPEPClient.class);
        }

    }

    public static class DataPreApprovedOfferFactory {

        public static CreatePreApprovedOfferMessage getDataPreApprovedOffer() throws JsonProcessingException {
            URL url = Resources.getResource("mocks/pre-approvedOffer/pre-approved-offer-response.json");
            String contentFile = Try.of(() -> new BufferedReader(new FileReader(url.getPath())))
                    .map(br -> br.lines().collect(Collectors.joining()))
                    .get();

            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(contentFile, CreatePreApprovedOfferMessage.class);
        }

    }
}
