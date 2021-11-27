#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname "$ABSPATH")
source "${ABSDIR}"/profile.sh
source "${ABSDIR}"/switch.sh

IDLE_PORT=$(find_idle_port)

echo "> Health Check Start"
echo "> IDLE_PORT: $IDLE_PORT"
echo "> curl -s http://localhost:$IDLE_PORT/profile"
sleep 10

for RETRY_COUNT in {1..10}
do
    RESPONSE=$(curl -s http://localhost:"${IDLE_PORT}"/profile)
    UP_COUNT=$(echo "${RESPONSE}" | grep 'prod' | wc -l)

    if [ "${UP_COUNT}" -ge 1 ]
    then
        # exists 'prod' string
        echo "> Health Check Success"
        switch_proxy
        break
    else
        echo "> Health Check Failed ${RETRY_COUNT}"
        echo "> Health Check: ${RESPONSE}"
    fi

    if [ "${RETRY_COUNT}" -eq 10 ]
    then
        echo "> Health Check Finished without success"
        echo "> Finish deployment without connecting nginx"
        exit 1
    fi

    echo "> Health check connecting failed. Retrying..."
    sleep 10
done
