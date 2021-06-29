FROM openjdk:8-jdk-alpine
WORKDIR /opt
ENV FIREBASE_SECRET="null"
ADD build/libs/clients-business-capability-starter-*SNAPSHOT.jar /opt/app.jar
ADD build/resources/main/newrelic/newrelic.jar /opt/newrelic.jar
ADD script.sh /opt/.

RUN sh -c 'touch /opt/app.jar'
RUN chmod +x /opt/script.sh

ENTRYPOINT ["sh","/opt/script.sh"]
