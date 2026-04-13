#!/usr/bin/env sh

# Source this file after filling in real values:
#   source ./env.example.sh

export NACOS_ADDR="127.0.0.1:8848"
export NACOS_NAMESPACE=""
export NACOS_GROUP="DEFAULT_GROUP"

export MYSQL_HOST="127.0.0.1"
export MYSQL_PORT="3306"
export MYSQL_USERNAME="root"
export MYSQL_PASSWORD="replace-me"

export REDIS_HOST="127.0.0.1"
export REDIS_PORT="6379"
export REDIS_PASSWORD=""

export ROCKETMQ_NAMESRV="127.0.0.1:9876"

export ALIPAY_APP_ID="mock_alipay_app_id"
export ALIPAY_PRIVATE_KEY="replace-me"
export ALIPAY_PUBLIC_KEY="replace-me"

export WECHAT_APP_ID="mock_wechat_app_id"
export WECHAT_MCH_ID="mock_wechat_mch_id"
export WECHAT_API_KEY="replace-me"
export WECHAT_CERT_PATH="/etc/certs/wechat/"
