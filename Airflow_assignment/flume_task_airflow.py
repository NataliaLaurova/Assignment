from airflow import DAG
from airflow.operators.dummy_operator import DummyOperator
from airflow.operators.bash_operator import BashOperator
from datetime import datetime

#Defaults
default_args = {
    'depends_on_past': False,
    'start_date': datetime(2019, 11, 11)
}

dag = DAG('Send_HTTP_to_HDFS', default_args=default_args, schedule_interval='@daily')

# Task
flume_command = "flume-ng agent --name agent_foo --conf-file /home/hadoop/flume/conf/flume_local.conf "
curl_command = "/home/hadoop/airflow/curl_command.sh "

kick_off_dag = DummyOperator(task_id='kick_off_dag', dag=dag)

flume_task = BashOperator(
task_id='Flume_Start',
bash_command=flume_command,
dag=dag)

sleep_com = BashOperator(
    task_id='sleep_operator',
    bash_command="sleep 2m ",
    dag=dag)

curl_task = BashOperator(
task_id='HTTP_Request',
bash_command=curl_command,
dag=dag)

kick_off_dag >> flume_task

kick_off_dag >> sleep_com >> curl_task
