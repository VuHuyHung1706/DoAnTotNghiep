FROM maven:3.9.9-amazoncorretto-23 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

FROM amazoncorretto:24

# 👉 Install timezone data for Amazon Linux
RUN yum install -y tzdata && \
    yum clean all

# 👉 Set timezone to Vietnam
ENV TZ=Asia/Ho_Chi_Minh
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]