package com.lulobank.clients.services.utils;

public enum LogMessages {
  CLIENT_NOT_FOUND_IN_DB_EXCEPTION("Client not found {}"),
  DYNAMO_ERROR_EXCEPTION("Dynamo error {}"),
  COGNITO_ERROR_EXCEPTION("Cognito error: {} {}"),
  JWT_GENERATION_ERROR_EXCEPTION("JWT generation error: {} {}"),
  EMAIL_EXIST_IN_COGNITO("Cognito error, email exist in cognito; {} {}"),
  GENERAL_EXCEPTION("General Exception {}"),
  SERVICE_EXCEPTION("Service Exception message {} - code {}"),
  RISK_SCORE_UPDATE_MSG("Risk score update for client {}"),
  ERROR_FIREBASE_CONFIG("Error firebase config: {} {}"),
  CLIENT_CREATED_FIREBASE("Client created in firebase {}"),
  START_BIOMETRIC_REPORT("Start the biometric identification process, with idTransaction: {}"),
  CLIENT_UPDATED_BIOMETRIC_IDENTITY_REPORT(
      "Start process to Identity Biometric report, idTransaction: {}"),
  ERROR_BIOMETRIC_IDENTITY_ID_NOT_FOUND("Error Identity Biometric Id {} not found on DB "),
  CLIENT_REJECTED_FIREBASE("Client rejected in firebase {}"),
  ERROR_UPDATE_FIREBASE("Error updating client in firebase clientId {} {}"),
  ERROR_FIREBASE_CLIENT("Error getting firebase client {} clientId :{}"),

  SAVING_ACCOUNT_CREATED_FIREBASE("Saving account client created in firebase {}"),
  CLIENT_APPLICANT_UPDATED("Client  {} updated with applicant {}"),
  CLIENT_EMAIL_VERIFIED("Client {} with email {} is now verified"),
  EMAIL_UPDATE_SUCCESSFUL("Email of Client {} was successfully updated to {}"),
  ERROR_UPDATING_EMAIL("Error updating email of client. Cause: {}"),
  ERROR_NOTIFYING_EMAIL_UPDATE("Error notifying email update. New email {} for client {}. Cause: {}"),
  ACTION_ON_PROVIDER_FAILED("Email Update on identity provider failed for user {}. Cause: {}"),
  EMAIL_UPDATE_ON_FLEXIBILITY_FAILED("Email Update on flexibility failed for user {}. Cause: {}"),
  ERROR_GENERATING_HASH("Error generating hash: {} {}"),
  EVENT_SENT_TO_RISK_ENGINE("Event sent to risk engine for clientId: {}"),
  EVENT_SENT_TO_SQS("Send event : {} , {}  to sqs {}, message json {}"),
  EVENT_NOT_MANAGED("Event Type  : {} , not managed for this handler"),
  EVENT_NOT_FOUND("Event Type  : {} , not support for this queue"),
  LOAN_REQUESTED_CREATED_FIREBASE("Saving account client created in firebase {}"),
  RISK_SCORE_UPDATE_MSG_FROM_LOAN_REQUESTED("Risk score update for client from Loan Requested {}"),
  RISK_SCORE_UPDATE_MSG_FROM_ONBORDING("Risk score update for client from Onbording {}"),
  EVENT_NOT_FORMAT("Event has bad format, {}"),
  USER_NOT_AUTORIZED("Not authorized {} {}"),
  CHECKPOINT_UPDATE_ERROR("Error while processing event to update checkpoint {}"),
  ERROR_SENDING_RISK_ENGINE_EVENT("Error while sending message to risk engine for clientId: {}"),
  ERROR_GENERATING_OFFERS(
      "Error while generating offers in credits microservice for clientId: {},{}"),
  FIREBASE_LOAN_ONBOARDING_FAILED("Onboarding loan client was updated with fail detail."),
  FIREBASE_LOAN_FROM_HOME_FAILED("From home loan client was updated with fail detail."),
  ERROR_GETTING_HEADER("Error getting Authorization header for idClient: {}"),
  ERROR_CORE_BANKING("Error in core banking : {}"),
  RISK_ENGINE_RESPONSE("Risk Engine response information to id Client : {}"),
  ERROR_BAD_PAYLOAD("Error getting Payload : {}"),
  NOT_LOAN_AMOUNT_TO_CLIENT("Impossible to process event, client don't have onboarding , : {}"),
  CLIENT_NOT_FOUND_UPDATING_PROFILE("Client not found while updating profile. Client Id {}."),
  DYNAMO_DB_ERROR_UPDATING_PROFILE(
      "DynamoDB error while updating profile. Client Id {}. With error: {}"),
  CLIENT_NOT_FOUND_GETTING_DEMOGRAPHIC_INFO(
      "Client not found while getting demographic information. Client Id {}."),
  ECONOMIC_INFORMATION_IS_NOT_PRESENT("Client doesn't  have Economic Information: {}"),
  INVALID_REQUEST_REASONS("Invalid request reasons: {}")
  ;
  private String message;

  LogMessages(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
