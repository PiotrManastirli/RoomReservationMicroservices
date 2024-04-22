# Используйте подходящий базовый образ
FROM openjdk:17-jdk

# Установите рабочую директорию в контейнере
WORKDIR /app

# Скопируйте собранный JAR файл из вашего таргет каталога
COPY target/*.jar app.jar

# Скопируйте конфигурационный файл в контейнер
COPY src/main/resources/application-docker.properties /app/config/application-docker.properties

# Установите переменную окружения для указания местоположения конфигурации
ENV SPRING_CONFIG_LOCATION=file:/app/config/application-docker.properties

# Запустите ваше приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
