#!/bin/bash
COMPLETE="путь_к_конечным_файлам"
CHAT_ID="сюда_чат_айди"
TELEGRAM_BOT="сюда_айди_телеграм_бота"

#Функция для обработки папки
dir () {
        #Создаю папку для медиасервера
        mkdir -p $COMPLETE/$TR_TORRENT_NAME
        #Прохожу по всем файлам в папке
        for movie in $(ls $TR_TORRENT_DIR/$TR_TORRENT_NAME)
        do
                mv $TR_TORRENT_DIR/$TR_TORRENT_NAME/$movie $COMPLETE/$movie
                #Если фильм ави
#                if [[ $movie == *avi ]]; then
#                        #Конвертирую его в mkv
#                        ffmpeg -i $TR_TORRENT_DIR/$TR_TORRENT_NAME/$movie $COMPLETE/$TR_TORRENT_NAME/$movie.mkv
#                #Если фильм mkv
#                elif [[ $movie == *mkv ]]; then
#                        #Перемещаю фильм в конечную папку
#                        mv $TR_TORRENT_DIR/$TR_TORRENT_NAME/$movie $COMPLETE/$movie
#                #Если непонятный файл, отправляю его в /tmp
#                else
#                        mkdir /tmp/$TR_TORRENT_NAME
#                        mv $TR_TORRENT_DIR/$TR_TORRENT_NAME/$movie /tmp/$TR_TORRENT_NAME/$movie
#                        echo -e "$movie\nбыл непонятен скрипту. Нужно сюда заглянуть\n/tmp/$TR_TORRENT_NAME/$movie" >> /tmp/$TR_TORRENT_NAME.message
#                fi
        done
#        echo -e "Торент\n$TR_TORRENT_NAME\nзагружен" >> /tmp/$TR_TORRENT_NAME.message
}

#Если скачался просто файл
file () {
        mv $TR_TORRENT_DIR/$TR_TORRENT_NAME $COMPLETE/$TR_TORRENT_NAME
        #Так же проверяю на формат
#        if [[ $TR_TORRENT_DIR/$TR_TORRENT_NAME == *avi ]]; then
#                #Крнвертирую
#                ffmpeg -i $TR_TORRENT_DIR/$TR_TORRENT_NAME $COMPLETE/$TR_TORRENT_NAME.mkv
#        elif [[ $TR_TORRENT_DIR/$TR_TORRENT_NAME == *mkv ]]; then
#                #Перемещаю
#                mv $TR_TORRENT_DIR/$TR_TORRENT_NAME $COMPLETE/$TR_TORRENT_NAME
#        else
#                mkdir /tmp/$TR_TORRENT_NAME
#                mv $TR_TORRENT_DIR/$TR_TORRENT_NAME /tmp/$TR_TORRENT_NAME/$TR_TORRENT_NAME
#                echo -e "Торрент\n$TR_TORRENT_NAME\nзагружен и не понятен скрипту" >> /tmp/$TR_TORRENT_NAME.message
#        fi
#        echo -e "Торрент\n$TR_TORRENT_NAME\nзагружен" >> /tmp/$TR_TORRENT_NAME.message
}

#send_message () {
#        curl https://api.telegram.org/bot$TELEGRAM_BOT/sendMessage?parse_mode=markdown -d chat_id=$CHAT_ID -d text="$(</tmp/$TR_TORRENT_NAME)"
#}

#echo "Start" >> /tmp/torr.txt
#echo $TR_TORRENT_DIR >> /tmp/torr.txt
#echo $TR_TORRENT_NAME >> /tmp/torr.txt
#echo $movie >> /tmp/torr.txt
#echo "Finish" >> /tmp/torr.txt

# if [[ -d $TR_TORRENT_DIR/$TR_TORRENT_NAME ]]; then
#         dir
# else
#         file
# fi
# send_message

#Удаляю старые файлы если они есть
# rm -rf $TR_TORRENT_DIR/$TR_TORRENT_NAME
# rm -rf /tmp/$TR_TORRENT_NAME.message