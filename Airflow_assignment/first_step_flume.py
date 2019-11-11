from airflow import DAG
from airflow.operators.bash_operator import BashOperator
from datetime import datetime

#Defaults
default_args = {
    'start_date': datetime(2019, 11, 10)
}

dag = DAG('Run_flume', default_args=default_args, schedule_interval='@daily')

# Task
flume_command = "flume-ng agent --name agent_foo --conf-file /home/hadoop/flume/conf/flume_local.conf "

flume_task = BashOperator(
task_id='Flume_Start',
bash_command=flume_command,
dag=dag)
