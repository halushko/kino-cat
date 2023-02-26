@echo off
setlocal EnableDelayedExpansion

set V_BUILD="false"
set V_DOWNLOAD="false"
set V_PUSH="false"
set V_TAG="latest"
set V_BRANCH="master"
set V_LATEST="false"
set V_RELEASE="false"
set V_VALUE=""
set V_ST="false"

setlocal EnableDelayedExpansion

for %%x in (%*) do (
    if /i "%%x" EQU "-h" (
        goto :usage
    )
    if /i "%%x" EQU "--help" (
        goto :usage
    )
    if /i "%%x" EQU "-b" (
        set V_BUILD="true"
    )
    if /i "%%x" EQU "-d" (
        set V_DOWNLOAD="true"
    )
    if /i "%%x" EQU "-p" (
        set V_PUSH="true"
    )
    if !V_VALUE! == "-t" (
        set V_TAG="%%x"
        set V_VALUE=""
    )
    if /i "%%x" EQU "-t" (
        set V_VALUE="%%x"
    )
    if !V_VALUE! == "--branch" (
        set V_BRANCH="%%x"
        set V_VALUE=""
    )
    if /i "%%x" EQU "--branch" (
        set V_VALUE="%%x"
        set V_BUILD="true"
        set V_DOWNLOAD="true"
    )
    if /i "%%x" EQU "--release" (
        set V_LATEST="true"
    )
    if /i "%%x" EQU "--full" (
        set V_BUILD="true"
        set V_DOWNLOAD="true"
        set V_PUSH="true"
    )
    if "%%x" == "" (
        goto :usage
    )
)

echo Build started

if %V_TAG% == "latest" (
    set V_LATEST="false"
)

echo V_BUILD=%V_BUILD%
echo V_DOWNLOAD=%V_DOWNLOAD%
echo V_PUSH=%V_PUSH%
echo V_TAG=%V_TAG%
echo V_BRANCH=%V_BRANCH%
echo V_LATEST=%V_LATEST%
echo V_RELEASE=%V_RELEASE%
echo V_VALUE=%V_VALUE%

set "tmp_dir=%cd%\tmp"
if exist "%tmp_dir%" rd /s /q "%tmp_dir%"
mkdir "%tmp_dir%"
cd "%tmp_dir%" || exit 0
echo tmp directory created

echo The TAG is %V_TAG%

if %V_LATEST% == "true" (
    echo The latest TAG will be built too
)

if %V_DOWNLOAD% == "true" (
    echo Download Dockerfiles from repo
    git init kino-cat
    cd kino-cat || exit /b 0
    git remote add origin https://github.com/halushko/kino-cat.git
    git config core.sparseCheckout true
    echo config/dockerfiles/ >>.git/info/sparse-checkout
    git pull origin "%V_BRANCH%"
    cd ..
) else (
    echo Copy Dockerfiles from current directory
    mkdir -p ./kino-cat/config/dockerfiles
    xcopy /s /y ..\Dockerfile-* .\kino-cat\config\dockerfiles
)

if %V_BUILD%=="true" (
    echo Start build tag = %V_TAG%
    cd ./kino-cat/config/dockerfiles || exit /b 0

    ::docker build --build-arg BRANCH="%V_BRANCH%" --build-arg ARCH=arm64v8/ --platform linux/arm64 -t halushko/cinema-middleware:tmp-%V_TAG%-arm64 -f Dockerfile-middleware .
    ::docker build --build-arg BRANCH="%V_BRANCH%" --build-arg ARCH=amd64/ --platform=linux/amd64 -t halushko/cinema-middleware:tmp-%V_TAG%-amd64 -f Dockerfile-middleware .
    ::docker build --build-arg BRANCH="%V_BRANCH%" --build-arg ARCH=arm64v8/ --platform=linux/arm64 -t halushko/cinema-middleware:%V_TAG%-arm64 -f Dockerfile-middleware .
    ::docker build --build-arg BRANCH="%V_BRANCH%" --build-arg ARCH=amd64/ --platform=linux/amd64 -t halushko/cinema-middleware:%V_TAG%-amd64 -f Dockerfile-middleware .

    ::docker push halushko/cinema-middleware:tmp-%V_TAG%-arm64
    ::docker push halushko/cinema-middleware:tmp-%V_TAG%-amd64
    ::docker manifest create halushko/cinema-middleware:tmp -a halushko/cinema-middleware:%V_TAG%-amd64 -a halushko/cinema-middleware:%V_TAG%-arm64
    ::docker manifest push halushko/cinema-middleware:tmp


    ::docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-bot:%V_TAG%-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-bot .
    docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-bot:%V_TAG%-amd64 --build-arg ARCH=amd64/ -f Dockerfile-bot .
    ::docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-file:%V_TAG%-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-file .
    docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-file:%V_TAG%-amd64 --build-arg ARCH=amd64/ -f Dockerfile-file .
    ::docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-media:%V_TAG%-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-minidlna .
    docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-media:%V_TAG%-amd64 --build-arg ARCH=amd64/ -f Dockerfile-minidlna .
    ::docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-text:%V_TAG%-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-text .
    docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-text:%V_TAG%-amd64 --build-arg ARCH=amd64/ -f Dockerfile-text .
    ::docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-torrent:%V_TAG%-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-torrent .
    docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-torrent:%V_TAG%-amd64 --build-arg ARCH=amd64/ -f Dockerfile-torrent .

    if %V_LATEST% == "true" (
        echo Start build 'latest' tag
        ::docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-middleware:latest-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-middleware .
        docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-middleware:latest-amd64 --build-arg ARCH=amd64/ -f Dockerfile-middleware .

        ::docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-bot:latest-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-bot .
        docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-bot:latest-amd64 --build-arg ARCH=amd64/ -f Dockerfile-bot .
        ::docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-file:latest-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-file .
        docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-file:latest-amd64 --build-arg ARCH=amd64/ -f Dockerfile-file .
        ::docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-media:latest-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-minidlna .
        docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-media:latest-amd64 --build-arg ARCH=amd64/ -f Dockerfile-minidlna .
        ::docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-text:latest-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-text .
        docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-text:latest-amd64 --build-arg ARCH=amd64/ -f Dockerfile-text .
        ::docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-torrent:latest-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-torrent .
        docker build --build-arg BRANCH="%V_BRANCH%" -t halushko/cinema-torrent:latest-amd64 --build-arg ARCH=amd64/ -f Dockerfile-torrent .
    )

    cd ../../..
    echo Finish build
)
if %V_PUSH% == "true" (
    echo Start push to Docker Hub tag %V_TAG%
    ::docker push halushko/cinema-middleware:%V_TAG%-arm64
    ::docker push halushko/cinema-middleware:%V_TAG%-amd64
    ::docker manifest create halushko/cinema-middleware:%V_TAG% -a halushko/cinema-middleware:%V_TAG%-amd64 -a halushko/cinema-middleware:%V_TAG%-arm64
    ::docker manifest push halushko/cinema-middleware:%V_TAG%
    ::docker push halushko/cinema-bot:%V_TAG%-arm64
    docker push halushko/cinema-bot:%V_TAG%-amd64
    docker manifest create halushko/cinema-bot:%V_TAG% -a halushko/cinema-bot:%V_TAG%-amd64 -a halushko/cinema-bot:%V_TAG%-arm64
    docker manifest push halushko/cinema-bot:%V_TAG%
    ::docker push halushko/cinema-file:%V_TAG%-arm64
    docker push halushko/cinema-file:%V_TAG%-amd64
    docker manifest create halushko/cinema-file:%V_TAG% -a halushko/cinema-file:%V_TAG%-amd64 -a halushko/cinema-file:%V_TAG%-arm64
    docker manifest push halushko/cinema-file:%V_TAG%
    ::docker push halushko/cinema-media:%V_TAG%-arm64
    docker push halushko/cinema-media:%V_TAG%-amd64
    docker manifest create halushko/cinema-media:%V_TAG% -a halushko/cinema-media:%V_TAG%-amd64 -a halushko/cinema-media:%V_TAG%-arm64
    docker manifest push halushko/cinema-media:%V_TAG%
    ::docker push halushko/cinema-text:%V_TAG%-arm64
    docker push halushko/cinema-text:%V_TAG%-amd64
    docker manifest create halushko/cinema-text:%V_TAG% -a halushko/cinema-text:%V_TAG%-amd64 -a halushko/cinema-text:%V_TAG%-arm64
    docker manifest push halushko/cinema-text:%V_TAG%
    ::docker push halushko/cinema-torrent:%V_TAG%-arm64
    docker push halushko/cinema-torrent:%V_TAG%-amd64
    docker manifest create halushko/cinema-torrent:%V_TAG% -a halushko/cinema-torrent:%V_TAG%-amd64 -a halushko/cinema-torrent:%V_TAG%-arm64
    docker manifest push halushko/cinema-torrent:%V_TAG%

    if %V_LATEST% == "true" (
        echo Start push to Docker Hub tag latest
        ::docker push halushko/cinema-middleware:latest-arm64
        docker push halushko/cinema-middleware:latest-amd64
        docker manifest create halushko/cinema-middleware:latest -a halushko/cinema-middleware:latest-amd64 -a halushko/cinema-middleware:latest-arm64
        docker manifest push halushko/cinema-middleware:latest
        ::docker push halushko/cinema-bot:latest-arm64
        docker push halushko/cinema-bot:latest-amd64
        docker manifest create halushko/cinema-bot:latest -a halushko/cinema-bot:latest-amd64 -a halushko/cinema-bot:latest-arm64
        docker manifest push halushko/cinema-bot:latest
        ::docker push halushko/cinema-file:latest-arm64
        docker push halushko/cinema-file:latest-amd64
        docker manifest create halushko/cinema-file:latest -a halushko/cinema-file:latest-amd64 -a halushko/cinema-file:latest-arm64
        docker manifest push halushko/cinema-file:latest
        ::docker push halushko/cinema-media:latest-arm64
        docker push halushko/cinema-media:latest-amd64
        docker manifest create halushko/cinema-media:latest -a halushko/cinema-media:latest-amd64 -a halushko/cinema-media:latest-arm64
        docker manifest push halushko/cinema-media:latest
        ::docker push halushko/cinema-text:latest-arm64
        docker push halushko/cinema-text:latest-amd64
        docker manifest create halushko/cinema-text:latest -a halushko/cinema-text:latest-amd64 -a halushko/cinema-text:latest-arm64
        docker manifest push halushko/cinema-text:latest
        ::docker push halushko/cinema-torrent:latest-arm64
        docker push halushko/cinema-torrent:latest-amd64
        docker manifest create halushko/cinema-torrent:latest -a halushko/cinema-torrent:latest-amd64 -a halushko/cinema-torrent:latest-arm64
        docker manifest push halushko/cinema-torrent:latest
    )
)

cd ..
rd /s /q tmp
echo tmp directory removed

echo Build finished
goto :end
:usage
echo.
echo Build docker images
echo Usage: %0 [--full] [-b] [-d] [-p] [-t] [--branch] [--release]
echo.
echo Options:
echo     --full        :   download dockerfiles + build images + push images to dockerhub
echo     -b            :   build Dockerfiles
echo     -d            :   use this flag to download Dockerfiles from github repo. Without this flag the Dockerfiles
echo                       from current directory will be used. No need to use with also see flag [--branch]
echo     --branch      :   see flag [-d]. This flag used to specify which branch of github is need to be used. By
echo                       default the "master" branch is used
echo     -p            :   pull images to DockerHub after completion
echo     -t            :   tag (name) of images that will be built
echo     --release     :   build images with tag [-t] and also build with tag "latest"
echo.
:end
exit /b 1