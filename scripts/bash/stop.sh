#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)

ABSDIR=$(dirname $ABSPATH)

source "${ABSDIR}"/profile.sh

IDLE_PORT=$(find_idle_port)

echo "> check pid of application running on $IDLE_PORT"
IDLE_PID=$(lsof -ti tcp:"${IDLE_PORT}")

if [ -z "${IDLE_PID}" ]
then
    echo "> do not stop any application"
else
    echo "> kill -15 $IDLE_PID"
    kill -15 "${IDLE_PID}"
    sleep 5
fi
