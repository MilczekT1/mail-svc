FROM konradboniecki/budget:java-base-image-290
ARG ARTIFACT=mail-*.jar
ADD /target/$ARTIFACT app.jar
ENTRYPOINT ["java", "-jar", \
    "-Djava.security.egd=file:/dev/./urandom", "app.jar", \
    "--spring.profiles.active=default" \
]
