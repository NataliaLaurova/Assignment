from airflow import DAG
from airflow.operators.bash_operator import BashOperator
from datetime import datetime

#Defaults
default_args = {
    'start_date': datetime(2019, 11, 10)
}

dag = DAG('Run_curl', default_args=default_args, schedule_interval='@daily')

# Task
curl_command = "/home/hadoop/airflow/curl_command.sh "

sleep_com = BashOperator(
    task_id='sleep_operator',
    bash_command="sleep 1m ",
    dag=dag)

curl_task = BashOperator(
task_id='HTTP_Request',
bash_command=curl_command,
dag=dag)

sleep_com >> curl_task
