#!/bin/bash

HOST=${1:-localhost}
PORT=${2:-5432}
SQL='select edu_person_principal_name, avatar_image from user_settings us join user_account ua on us.user_id=ua.id where avatar_image is not null'
WORKDIR=$(mktemp -d)

cd $WORKDIR

psql -h $HOST -A -t -F ";" -p $PORT -U opintoni opintoni -c "$SQL" > images.csv

mkdir avatar

while IFS=\; read -r eppn avatar
do
    echo -n $avatar | xxd -r -p > avatar/$eppn.png
done < images.csv

tar vczf avatar.tar.gz avatar

rm images.csv
rm -r avatar

echo ""
echo Avatar images saved to `pwd`/avatar.tar.gz
echo ""
echo Copy archive to file storage server:
echo scp `pwd`/avatar.tar.gz `whoami`@files-X-18.it.helsinki.fi:.
echo ""
echo and run commands on files server:
echo "  tar vxzf avatar.tar.gz"
echo "  sudo cp -rnv avatar/* /var/opt/data/minio/data/obar/avatar/"
