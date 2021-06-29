package com.lulobank.clients.starter.v3.adapters.out.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.lulobank.clients.starter.outboundadapter.dynamo.DynamoDBLocal;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.tracing.BraveTracerWrapper;
import com.lulobank.tracing.DatabaseBrave;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DynamoDBLocal.class})
public class ClientsV3RepositoryConfig {

    @Bean
    public ClientsV3Repository clientsV3Repository(DynamoDBMapper dynamoDBMapper,
                                                   DynamoDB dynamoDB,
                                                   BraveTracerWrapper wrapper) {
        return new ClientsAdapterV3Repository(dynamoDBMapper, dynamoDB, new DatabaseBrave(wrapper,"Clients"));
    }

}
