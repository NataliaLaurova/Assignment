CREATE DATABASE enhanceit
LOCATION 'hive/enhance_dbs';

USE enhanceit;

CREATE EXTERNAL TABLE engineers
        (Id int,
        First_name string,
        Last_name string,
        Age int,
        City string,
        Country string,
        Salary string,
        department string)
ROW FORMAT DELIMITED
LOCATION '/hive/enhance_dbs/engineers'
FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\n'
STORED AS TEXTFILE
;

CREATE EXTERNAL TABLE managers LIKE engineers
LOCATION '/hive/enhance_dbs/managers'
FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\n'
STORED AS TEXTFILE
;

CREATE EXTERNAL TABLE departments
        (Id int,
        Name string)
LOCATION '/hive/enhance_dbs/managers'
FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\n'
STORED AS TEXTFILE
;

LOAD DATA LOCAL INPATH '/home/maria_dev/input.txt' OVERWRITE INTO TABLE engineers;

INSERT INTO managers VALUES
(1, "Olson", "Dimanche", 25, "Atlanta", "USA", 120000, 1)
;

INSERT INTO departments VALUES
(1, "Big Data")
;

ALTER TABLE engineers
ADD COLUMNS (hire_date DATE,
    renewal_date DATE)
;

INSERT OVERWRITE TABLE engineers
SELECT Id,
First_name,
Last_name,
Age,
City,
Country,
Salary,
Department,
COALESCE(hire_date,date'2019-10-29') as hire_date,
COALESCE(renewal_date, cast(DATE_ADD(ADD_MONTHS(date'2019-10-29', 12),0) as date)) renewal_date
FROM engineers
;

