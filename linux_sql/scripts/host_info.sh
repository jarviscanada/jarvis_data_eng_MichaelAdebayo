#!/bin/bash

psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

if [ "$#" -ne 5 ]; then
    echo "Illegal number of parameters"
    exit 1
fi

lscpu_out=`lscpu`
hostname=$(hostname -f)

cpu_number=$(echo "$lscpu_out"| egrep "^CPU\(s\):" | awk '{print $2}' | xargs)
cpu_architecture=$(echo "$lscpu_out" | awk '/Architecture:/ {print $2}')
cpu_model=$(echo "$lscpu_out" | awk '/Model:/ {print $2}')
l2_cache=$(echo "$lscpu_out"| awk '/^L2 cache:/ {print $3}')
total_mem=$(vmstat --unit M | tail -1 | awk '{print $4}')
timestamp=$(vmstat -t | awk '{print $18 , $19}'|tail -n1)
cpu_mhz=$(cat /proc/cpuinfo | egrep "^cpu MHz" | head -n 1 | awk '{print $4}')

host_id="(SELECT id FROM host_info WHERE hostname='$hostname')";

insert_stmt="INSERT INTO host_info(hostname,cpu_number, cpu_model ,cpu_mhz , cpu_architecture,  l2_cache, total_mem , timestamp)
VALUES('$hostname','$cpu_number','$cpu_model','$cpu_mhz','$cpu_architecture',
'$l2_cache','$total_mem','$timestamp')";

export PGPASSWORD=$psql_password
psql -h "$psql_host" -p "$psql_port" -d "$db_name" -U "$psql_user" -c "$insert_stmt"
exit $?