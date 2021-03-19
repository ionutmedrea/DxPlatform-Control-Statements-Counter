FROM openjdk:15

WORKDIR /control_stmt_counter

COPY control_stmt_counter.jar ./control_stmt_counter.jar

ENTRYPOINT ["java", "-jar", "control_stmt_counter.jar"]