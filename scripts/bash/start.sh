#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname "$ABSPATH")
source "${ABSDIR}"/profile.sh

REPOSITORY=/home/ec2-user/app
PROJECT_NAME=springboot-intro

echo "> copy build files"
echo "> cp $REPOSITORY/deploy/*.jar $REPOSITORY/"

cp $REPOSITORY/deploy/*.jar $REPOSITORY/

echo "> deploy new application"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> give +x to $JAR_NAME"

chmod +x "$JAR_NAME"

echo "> run $JAR_NAME"

IDLE_PROFILE=$(find_idle_profile)

echo "> run $JAR_NAME with profile=$IDLE_PROFILE"

nohup java -jar \
    -Dspring.profiles.active="$IDLE_PROFILE" \
    "$JAR_NAME" > $REPOSITORY/nohup-"$IDLE_PROFILE".out 2>&1 &
