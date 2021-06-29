package com.lulobank.clients.services.factory;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lulobank.clients.services.features.initialclient.model.CreateInitialClient;
import io.vavr.control.Try;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class EntityFactory {


    public static class CreateInitialClientFactory {

        public static CreateInitialClient createInitialClientValid() {
            URL url = Resources.getResource("json/CreateInitialClient.json");
            String contentFile = Try.of(() -> new BufferedReader(new FileReader(url.getPath())))
                    .map(br -> br.lines().collect(Collectors.joining()))
                    .get();

            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new Util.JsonDateTimeArrayDeserializer()).create();

            return gson.fromJson(contentFile, CreateInitialClient.class);
        }

    }
}
