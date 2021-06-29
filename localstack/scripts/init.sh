#! /bin/bash

until cat /tmp/supervisord.log  | grep Ready

do
  sleep .1
done


sleep 1

awslocal sqs create-queue --queue-name blacklist --attributes file:///scripts/q-attributes.json
awslocal sqs create-queue --queue-name client --attributes file:///scripts/q-attributes.json
awslocal sqs create-queue --queue-name riskengine --attributes file:///scripts/q-attributes.json
awslocal sqs create-queue --queue-name saving --attributes file:///scripts/q-attributes.json
awslocal sqs create-queue --queue-name reporting --attributes file:///scripts/q-attributes.json
awslocal sqs create-queue --queue-name client --attributes file:///scripts/q-attributes.json
awslocal sqs create-queue --queue-name notification --attributes file:///scripts/q-attributes.json

awslocal dynamodb create-table --table-name Clients \
    --attribute-definitions \
        AttributeName=idClient,AttributeType=S AttributeName=idCard,AttributeType=S \
    --key-schema AttributeName=idClient,KeyType=HASH AttributeName=idCard,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1


# clear log file
cat /dev/null > /tmp/supervisord.log
