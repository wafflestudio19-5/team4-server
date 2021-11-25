#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname "$ABSPATH")
source "${ABSDIR}"/profile.sh

function switch_proxy() {
    IDLE_PORT=$(find_idle_port)

    echo "> Port to convert: $IDLE_PORT"
    echo "> Convert port"
    echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc

    echo "> Reload nginx"
    # nignx reload.
    sudo service nginx reload
}
