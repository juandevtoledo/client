#!/bin/bash
mkdir -p /opt/firebase
printf "%s\n" "$FIREBASE_SECRET" > /opt/firebase/firebase-adminsdk.json

java -javaagent:newrelic.jar -Dloader.path=/opt -Djava.security.egd=file:/dev/./urandom -jar app.jar