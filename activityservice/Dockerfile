FROM eclipse-temurin:21-jdk

# 2. Maintainer Info (Optional)
LABEL maintainer="fitness-app"

# 3. Jar File ko container me copy karo
# Note: Pehle humein 'mvn clean package' chalana padega taaki target folder me jar ban jaye
COPY target/activityservice-0.0.1-SNAPSHOT.jar app.jar

# 4. App run karne ka command
ENTRYPOINT ["java", "-jar", "/app.jar"]