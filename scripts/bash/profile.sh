#!/usr/bin/env bash

function find_idle_profile() {
#    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/profile)
#
#    if [ "${RESPONSE_CODE}" -ge 400 ] # 40x or 50x
#    then
#        CURRENT_PROFILE=prod1
#    else
#        CURRENT_PROFILE=$(curl -s http://localhost/profile)
#    fi
#
#    # IDLE_PROFILE: profile that is not connected to nginx
#    if [ "${CURRENT_PROFILE}" == prod1 ]
#    then
#        IDLE_PROFILE=prod2
#    else
#        IDLE_PROFILE=prod1
#    fi
#
#    echo "${IDLE_PROFILE}"
     echo "prod1"
}

function find_idle_port() {
#    IDLE_PROFILE=$(find_idle_profile)
#
#    if [ "${IDLE_PROFILE}" == prod1 ]
#    then
#        echo "8081"
#    else
#        echo "8082"
#    fi
    echo "8081"
}
