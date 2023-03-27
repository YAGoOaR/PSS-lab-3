import mysql.connector

database = "mydb"

db = mysql.connector.connect(
    host="localhost",
	port=6000,
    user="root",
    password="mysecretpass"
)

cursor = db.cursor()
cursor.execute(f"DROP DATABASE {database}")
