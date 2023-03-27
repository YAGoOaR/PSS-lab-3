import mysql.connector

database = "mydb"

db = mysql.connector.connect(
    host="localhost",
	port=6000,
    user="root",
    password="mysecretpass"
)

cursor = db.cursor()
cursor.execute(f"CREATE DATABASE {database}")

db = mysql.connector.connect(
    host="localhost",
	port=6000,
    user="root",
    password="mysecretpass",
    database=database
)

cursor = db.cursor()

cursor.execute("CREATE TABLE table1 (id INT AUTO_INCREMENT PRIMARY KEY, col1 INT)")

for i in range(1, 16+1):
	cursor.execute(f"INSERT INTO table1 (id, col1) VALUES ({i}, {i})")

db.commit()
