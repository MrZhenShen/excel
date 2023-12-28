FROM openjdk:21

EXPOSE 8080

ENV WORKBOOK_LOCATION=/tmp/workbook.xlsx

COPY ../target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]