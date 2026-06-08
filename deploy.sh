
git pull
# -Dmaven.test.skip=true
mvn clean package install  -T 1C

pm2 restart dxctest
sleep 10
tail -f /data/logs/dxctest/*
