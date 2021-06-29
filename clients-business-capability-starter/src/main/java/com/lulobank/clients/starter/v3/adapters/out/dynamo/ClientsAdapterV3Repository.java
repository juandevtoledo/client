package com.lulobank.clients.starter.v3.adapters.out.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.dto.ClientEntity;
import com.lulobank.clients.starter.v3.mapper.ClientsEntityV3Mapper;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.IdentityBiometricV3;

import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.tracing.DatabaseBrave;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClientsAdapterV3Repository implements ClientsV3Repository {

    private static final String IDENTITY_BIOMETRIC_ID_KEY = ":identityBiometricId";
    private static final String ID_CBS_KEY = ":idCbs";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String PHONE_PREFIX = "phonePrefix";
    private static final String ID_CLIENT = "idClient";
    private static final String TABLE_CLIENTS = "Clients";

    private final DynamoDBMapper dynamoDBMapper;
    private final DynamoDB dynamoDB;
    private final ObjectMapper objectMapper;
    private final DatabaseBrave databaseBrave;


    public ClientsAdapterV3Repository(DynamoDBMapper dynamoDBMapper,
                                      DynamoDB dynamoDB,
                                      DatabaseBrave databaseBrave) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.dynamoDB = dynamoDB;
        this.databaseBrave = databaseBrave;
        this.objectMapper = new ObjectMapper();

    }

    @Override
    public Try<ClientsV3Entity> save(ClientsV3Entity clientsV3Entity) {
        return Try.of(() -> ClientsEntityV3Mapper.INSTANCE.toEntity(clientsV3Entity))
                .andThenTry(clientEntity -> databaseBrave.save(dynamoDBMapper::save).accept(clientEntity))
                .map(ClientsEntityV3Mapper.INSTANCE::toV3Entity)
                .onSuccess(a -> log.info("Client updated successful , idClient {}", clientsV3Entity.getIdClient()))
                .onFailure(error -> log.error("Error to trying persist client , idClient {} , msg {} ", clientsV3Entity.getIdClient(), error.getMessage()));
    }

    @Override
    public Option<ClientsV3Entity> findByIdClient(String idClient) {
        return Option.of(databaseBrave.query(() -> dynamoDBMapper.load(ClientEntity.class, idClient)))
                .map(ClientsEntityV3Mapper.INSTANCE::toV3Entity);
    }

    @Override
    public Option<ClientsV3Entity> findByIdentityBiometric(IdentityBiometricV3 identityBiometricV3) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(IDENTITY_BIOMETRIC_ID_KEY, new AttributeValue().withS(identityBiometricV3.getIdTransaction()));

        DynamoDBQueryExpression<ClientEntity> queryExpression = new DynamoDBQueryExpression<ClientEntity>()
                .withIndexName("identityBiometricId-index")
                .withKeyConditionExpression("identityBiometricId = " + IDENTITY_BIOMETRIC_ID_KEY)
                .withConsistentRead(false)
                .withExpressionAttributeValues(attributeValues);

        return Try.of(() -> databaseBrave.query(() -> dynamoDBMapper.query(ClientEntity.class, queryExpression).stream())
                .filter(clientEntity -> clientEntity.getIdentityBiometric().getStatus()
                        .equals(identityBiometricV3.getStatus())).findFirst())
                .onFailure(error -> log.error("Error to trying to find client by identity biometric id, msg {} ", error.getMessage()))
                .toOption()
                .flatMap(Option::ofOptional)
                .map(ClientsEntityV3Mapper.INSTANCE::toV3Entity)
                .peek(entity -> log.info("Client was found by IdentityBiometricId: " + entity.getIdClient()));
    }

    @Override
    public Option<ClientsV3Entity> findByIdCbs(String idCbs) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(ID_CBS_KEY, new AttributeValue().withS(idCbs));

        DynamoDBQueryExpression<ClientEntity> queryExpression = new DynamoDBQueryExpression<ClientEntity>()
                .withIndexName("idCbs-index")
                .withKeyConditionExpression("idCbs = " + ID_CBS_KEY)
                .withConsistentRead(false)
                .withExpressionAttributeValues(attributeValues);

        return Try.of(() -> databaseBrave.query(() -> dynamoDBMapper.query(ClientEntity.class, queryExpression).stream().findFirst()))
                .onFailure(error -> log.error("Error to trying to find client by idCbs, msg {} ", error.getMessage()))
                .toOption()
                .flatMap(Option::ofOptional)
                .map(ClientsEntityV3Mapper.INSTANCE::toV3Entity)
                .peek(entity -> log.info("Client was found by idCbs: " + entity.getIdClient()));
    }

    @Override
    public Option<ClientsV3Entity> findByIdCard(String idCard) {
        return findByOneStringAttribute(idCard, "idCard");
    }

    @Override
    public Option<ClientsV3Entity> findByEmailAddress(String email) {
        return findByOneStringAttribute(email, "emailAddress");
    }

    @Override
    public Option<ClientsV3Entity> findByPhonePrefixAndPhoneNumber(Integer phonePrefix, String phoneNumber) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":" + PHONE_NUMBER, new AttributeValue().withS(phoneNumber));
        eav.put(":" + PHONE_PREFIX, new AttributeValue().withN(phonePrefix.toString()));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression(PHONE_NUMBER + " = :" + PHONE_NUMBER + " AND " + PHONE_PREFIX + " = :" + PHONE_PREFIX)
                .withExpressionAttributeValues(eav);

        return Try.of(() -> databaseBrave.query(()-> dynamoDBMapper.scan(ClientEntity.class, scanExpression).stream().findFirst()))
                .onFailure(error -> log.error("Error to trying to find client by {}, msg {} ", phoneNumber, error.getMessage()))
                .toOption()
                .flatMap(Option::ofOptional)
                .map(ClientsEntityV3Mapper.INSTANCE::toV3Entity);
    }

    @Override
    public void updateOnBoarding(ClientsV3Entity clientsV3Entity) {
        Try.of(() -> getItemSpecByOnboarding(clientsV3Entity, getExpressionByOnboarding()))
                .peek(updateItemSpec -> update(updateItemSpec, clientsV3Entity.getIdClient()))
                .onFailure(error -> log.error("Error creating  UpdateItemSpec , idClient : {}, msg : {}", clientsV3Entity.getIdClient(), error.getMessage()));

    }

    @Override
    public Try<Void> updateClientBlacklisted(ClientsV3Entity clientsV3Entity) {
        return Try.of(() -> getItemSpecUpdateBlacklisted(clientsV3Entity))
                .flatMap(updateItemSpec -> updateClientItemSpec(updateItemSpec, clientsV3Entity.getIdClient()));
    }

    @Override
    public Either<UseCaseResponseError, Boolean> updatePhoneNumber(String idClient, String phoneNumber, Integer prefix) {
        return Try.of(() -> getItemSpecByPhoneNumber(idClient, phoneNumber, prefix))
                .flatMap(updateItemSpec -> update(updateItemSpec, idClient))
                .map(r -> true)
                .toEither(ClientsDataError.internalError());
    }

    private Option<ClientsV3Entity> findByOneStringAttribute(String value, String attributeName) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":".concat(attributeName), new AttributeValue().withS(value));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression(attributeName.concat("= :").concat(attributeName))
                .withExpressionAttributeValues(eav);

        return Try.of(() -> databaseBrave.query(()->dynamoDBMapper.scan(ClientEntity.class, scanExpression).stream().findFirst()))
                .onFailure(error -> log.error("Error to trying to find client by {}, msg {} ", attributeName, error.getMessage()))
                .toOption()
                .flatMap(Option::ofOptional)
                .map(ClientsEntityV3Mapper.INSTANCE::toV3Entity);
    }


    private UpdateItemSpec getItemSpecUpdateBlacklisted(ClientsV3Entity clientsV3Entity) {
        return new UpdateItemSpec().withPrimaryKey(ID_CLIENT, clientsV3Entity.getIdClient())
                .withUpdateExpression(new StringBuilder("set blackListState  = :blackListState,")
                        .append("blackListDate  = :blackListDate, ")
                        .append("whitelistExpirationDate  = :whitelistExpirationDate")
                        .toString())
                .withValueMap(new ValueMap().withString(":blackListState", clientsV3Entity.getBlackListState())
                        .withString(":blackListDate",
                                Option.of(clientsV3Entity.getBlackListDate()).map(LocalDateTime::toString).getOrNull())
                        .withString(":whitelistExpirationDate",
                                Option.of(clientsV3Entity.getWhitelistExpirationDate()).map(LocalDateTime::toString).getOrNull())
                )
                .withReturnValues(ReturnValue.UPDATED_NEW);
    }

    public Map<String, String> getExpressionByOnboarding() {
        final Map<String, String> expressions = new HashMap<>();
        expressions.put("#n", "name");
        return expressions;
    }

    public UpdateItemSpec getItemSpecByOnboarding(ClientsV3Entity clientsV3Entity, Map<String, String> expressions) throws JsonProcessingException {
        return new UpdateItemSpec().withPrimaryKey(ID_CLIENT, clientsV3Entity.getIdClient())
                .withUpdateExpression(new StringBuilder("set blackListState  = :blackListState,")
                        .append("onBoardingStatus.checkpoint  = :checkPoint, ")
                        .append("idCard  = :idCard, ")
                        .append("#n  = :name, ")
                        .append("lastName  = :lastName, ")
                        .append("typeDocument  = :typeDocument,")
                        .append("expirationDate  = :expirationDate,")
                        .append("additionalPersonalInformation  = :additionalPersonalInformation, ")
                        .append("idCbs  = :idCbs, ")
                        .append("digitalStorageStatus  = :digitalStorageStatus, ")
                        .append("catsDocumentStatus  = :catsDocumentStatus, ")
                        .append("customerCreatedStatus  = :customerCreatedStatus, ")
                        .append("blackListDate  = :blackListDate, ")
                        .append("identityBiometric  = :identityBiometric,")
                        .append("gender  = :gender,")
                        .append("dateOfIssue  = :dateOfIssue,")
                        .append("birthDate  = :birthDate,")
                        .append("identityProcessed  = :identityProcessed")
                        .toString())
                .withNameMap(expressions)
                .withValueMap(new ValueMap().withString(":blackListState", clientsV3Entity.getBlackListState())
                        .withString(":checkPoint", clientsV3Entity.getOnBoardingStatus().getCheckpoint())
                        .withString(":name", clientsV3Entity.getName())
                        .withString(":idCard", clientsV3Entity.getIdCard())
                        .withString(":lastName", clientsV3Entity.getLastName())
                        .withString(":typeDocument", clientsV3Entity.getTypeDocument())
                        .withString(":expirationDate", clientsV3Entity.getExpirationDate())
                        .withJSON(":additionalPersonalInformation", objectMapper.writeValueAsString(clientsV3Entity.getAdditionalPersonalInformation()))
                        .withString(":idCbs", clientsV3Entity.getIdCbs())
                        .withBoolean(":digitalStorageStatus", clientsV3Entity.isDigitalStorageStatus())
                        .withBoolean(":catsDocumentStatus", clientsV3Entity.isCatsDocumentStatus())
                        .withBoolean(":customerCreatedStatus", clientsV3Entity.isCustomerCreatedStatus())
                        .withString(":blackListDate", Option.of(clientsV3Entity.getBlackListDate()).map(LocalDateTime::toString).getOrNull())
                        .withJSON(":identityBiometric", objectMapper.writeValueAsString(clientsV3Entity.getIdentityBiometric()))
                        .withString(":gender", clientsV3Entity.getGender())
                        .withString(":dateOfIssue", Option.of(clientsV3Entity.getDateOfIssue()).map(LocalDate::toString).getOrNull())
                        .withString(":birthDate", Option.of(clientsV3Entity.getBirthDate()).map(LocalDate::toString).getOrNull())
                        .withBoolean(":identityProcessed", clientsV3Entity.isIdentityProcessed())
                )
                .withReturnValues(ReturnValue.UPDATED_NEW);
    }

    private Try<Void> updateClientItemSpec(UpdateItemSpec updateItemSpec, String idClient) {
        return Try.of(() -> dynamoDB.getTable(TABLE_CLIENTS))
                .andThenTry(table -> table.updateItem(updateItemSpec))
                .flatMap(table -> Try.run(() -> log.info("Client updated idClient{}", idClient)))
                .onFailure(e -> log.error("Error updating idClient {}:  message : {}", idClient, e.getMessage()));
    }

    private Try<Table> update(UpdateItemSpec updateItemSpec, String idClient) {
        return Try.of(() -> dynamoDB.getTable(TABLE_CLIENTS))
                .andThenTry(table -> table.updateItem(updateItemSpec))
                .onFailure(e -> log.error("Error updating idClient {}:  message : {}", idClient, e.getMessage()));
    }

    public UpdateItemSpec getItemSpecByPhoneNumber(String idClient, String phoneNumber, Integer prefix) {
        return new UpdateItemSpec().withPrimaryKey(ID_CLIENT, idClient)
                .withUpdateExpression(new StringBuilder("set phoneNumber  = :phoneNumber,")
                        .append("phonePrefix  = :phonePrefix ")
                        .toString())
                .withValueMap(new ValueMap().withString(":phoneNumber", phoneNumber)
                        .withInt(":phonePrefix", prefix))
                .withReturnValues(ReturnValue.UPDATED_NEW);
    }

    @Override
    public Try<Void> updateEmailByIdClient(String idClient, String emailAddress) {
        return Try.of(() -> new UpdateItemSpec().withPrimaryKey(ID_CLIENT, idClient)
                .withUpdateExpression("set emailAddress =:emailAddress")
                .withValueMap(new ValueMap().withString(":emailAddress", emailAddress))
                .withReturnValues(ReturnValue.UPDATED_NEW))
                .flatMap(updateItemSpec -> updateEmail(updateItemSpec, idClient, emailAddress));
    }

    private Try<Void> updateEmail(UpdateItemSpec updateItemSpec, String idClient, String emailAddress) {
        return Try.of(() -> dynamoDB.getTable(TABLE_CLIENTS))
                .andThenTry(table -> table.updateItem(updateItemSpec))
                .flatMap(table -> Try.run(() -> log.info("Email Address updated email : {} idClient : {}",
                        emailAddress, idClient)))
                .onFailure(e -> log.error("Error updating emailAddress : {} idClient : {} message : {}",
                        emailAddress, idClient, e.getMessage()));
    }

}
