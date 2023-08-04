FROM openjdk:11
ADD target/intranet.jar intranet.jar
ENTRYPOINT ["java", "-jar","intranet.jar","--spring.profiles.active=prod"]
EXPOSE 8080
RUN mkdir -p /Extraction/Archive && mkdir -p /Extraction/Societe/SAGMA && mkdir -p /Extraction/Societe/ORONE && mkdir -p /Extraction/Societe/PROCHECK
RUN chmod -R 777 /Extraction