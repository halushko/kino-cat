FROM alpine/git as download_image
RUN mkdir -p /home/git/code
RUN cd /home/git/code && \
    git init kino-cat && \
    cd kino-cat && \
    git remote add origin https://github.com/halushko/kino-cat.git && \
    git config core.sparseCheckout true && \
    echo "middleware/" >> .git/info/sparse-checkout && \
    echo "fileConsumer/" >> .git/info/sparse-checkout && \
    echo "textConsumer/" >> .git/info/sparse-checkout && \
    echo "bot/" >> .git/info/sparse-checkout && \
    echo "minidlna/" >> .git/info/sparse-checkout && \
    echo "torrent/" >> .git/info/sparse-checkout && \
    git pull origin master

FROM gradle:jdk8-alpine
VOLUME ["/home/gradle/jars"]
RUN mkdir -p /home/gradle/code
RUN mkdir -p /home/gradle/app

COPY --chown=gradle:gradle --from=download_image /home/git/code /home/gradle/code

RUN cd /home/gradle/code/kino-cat/middleware && gradle jar && cp ./build/libs/*.jar /home/gradle/app/kot_utils.jar
RUN cd /home/gradle/code/kino-cat/fileConsumer && gradle fatJar && cp ./build/libs/*.jar /home/gradle/app/file_consumer.jar
RUN cd /home/gradle/code/kino-cat/textConsumer && gradle fatJar && cp ./build/libs/*.jar /home/gradle/app/text_consumer.jar
RUN cd /home/gradle/code/kino-cat/bot && gradle fatJar && cp ./build/libs/*.jar /home/gradle/app/micro_bot.jar
RUN cd /home/gradle/code/kino-cat/minidlna && gradle fatJar && cp ./build/libs/*.jar /home/gradle/app/minidlna.jar
RUN cd /home/gradle/code/kino-cat/torrent && gradle fatJar && cp ./build/libs/*.jar /home/gradle/app/torrent.jar

CMD cp /home/gradle/app/*.jar /home/gradle/jars