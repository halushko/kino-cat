FROM alpine/git as download_image
RUN mkdir -p /home/git/code
RUN cd /home/git/code && \
    git init kino-cat && \
    cd kino-cat && \
    git remote add origin https://github.com/halushko/kino-cat.git && \
    git config core.sparseCheckout true && \
    echo "rabKot/" >> .git/info/sparse-checkout && \
    echo "torrent/" >> .git/info/sparse-checkout && \
    echo "config/commands/transmission/" >> .git/info/sparse-checkout && \
    echo "config/config_files" >> .git/info/sparse-checkout && \ /config/config_files && \
    git pull origin master

FROM gradle:jdk8-alpine as build_image
RUN mkdir -p /home/gradle/code
RUN mkdir -p /home/gradle/app

COPY --chown=gradle:gradle --from=download_image /home/git/code /home/gradle/code

RUN cd /home/gradle/code/kino-cat/rabKot && gradle jar && cp ./build/libs/*.jar /home/gradle/app/kot_utils.jar
RUN cd /home/gradle/code/kino-cat/torrent && gradle fatJar && cp ./build/libs/*.jar /home/gradle/app/torrent.jar

FROM openjdk:8-alpine
RUN apk add transmission-daemon transmission-cli

RUN mkdir -p /home/media/torrent/downloads && \
    mkdir -p /home/media/torrent/complete && \
    mkdir -p /home/media/torrent/torrent_files && \
    chown -R debian-transmission:debian-transmission /home/media/

COPY --chown=debian-transmission:debian-transmission --from=download_image /home/git/code/kino-cat/config/config_files/* /home/app/
COPY --chown=debian-transmission:debian-transmission --from=download_image /home/git/code/kino-cat/config/commands/transmission /home/config_files

RUN cp -f /home/config_files/settings.json /etc/transmission-daemon/settings.json
RUN cp /home/config_files/after_download.sh /etc/transmission-daemon/after_download.sh

COPY --from=build_image /home/gradle/app /home/app
ENTRYPOINT ["java", "-jar", "/home/app/torrent.jar"]