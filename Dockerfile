FROM fedora:39

RUN dnf update -y \
    && dnf install -y java-latest-openjdk

ARG JAR_FILE=target/fretquiz.jar

ADD ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
