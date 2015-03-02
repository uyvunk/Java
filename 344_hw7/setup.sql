--Vu Nguyen
-- CSE 344

--CREATE RELATIONSHIPS/TABLES
CREATE TABLE Plans (plan_id integer PRIMARY KEY, name varchar(20), maximum_number integer, monthly_fee integer);

CREATE TABLE Customer(id integer PRIMARY KEY, login varchar(10), password varchar(20), first_name varchar(50), last_name varchar(50), plan_id integer REFERENCES Plans(plan_id));

CREATE TABLE Rental(customer_id integer REFERENCES Customer(id), movie_id integer, status varchar(6), date_and_time varchar(30), PRIMARY KEY (customer_id, movie_id, date_and_time));

--INSERT VALUES INTO TABLES
INSERT INTO Customer VALUES (1, 'mega', '123456', 'Vu', 'Nguyen', 1);
INSERT INTO Customer VALUES (2, 'tera', '123456', 'Vy', 'Nguyen', 1);
INSERT INTO Customer VALUES (3, 'kilo', '111111', 'Truc', 'Nguyen', 2);
INSERT INTO Customer VALUES (4, 'mili', '222222', 'Alfian', 'Rizqi', 3);
INSERT INTO Customer VALUES (5, 'nano', '333333', 'Zhitao', 'Zhang', 4);
INSERT INTO Customer VALUES (6, 'micro', '444444', 'Thao Nguyen', 'Dang', 2);
INSERT INTO Customer VALUES (7, 'pico', '555555', 'Erin', 'Yoon', 3);
INSERT INTO Customer VALUES (8, 'teriyaki', '666666', 'Mehhong', 'Chhay', 4);

INSERT INTO Plans VALUES (1, 'Premium', 20, 20);
INSERT INTO Plans VALUES (2, 'Value+', 15, 15);
INSERT INTO Plans VALUES (3, 'Family', 10, 10);
INSERT INTO Plans VALUES (4, 'Simple', 5, 5);
INSERT INTO Plans VALUES (5, 'Saving', 4, 4);
INSERT INTO Plans VALUES (6, 'Econ +', 3, 3);
INSERT INTO Plans VALUES (7, 'Econ', 2, 1);
INSERT INTO Plans VALUES (8, 'Student', 10, 7);

INSERT INTO Rental VALUES (1,581605,'open', '022720151100'); --mmddyyyyhhmm
INSERT INTO Rental VALUES (4,254288,'open', '010120150000'); 
INSERT INTO Rental VALUES (2,517955,'open', '021220152300'); 
INSERT INTO Rental VALUES (5,83324,'open', '021220152100'); 
INSERT INTO Rental VALUES (2,83324,'open', '021220152100'); 
INSERT INTO Rental VALUES (3,124641,'close', '011020152100'); 
INSERT INTO Rental VALUES (3,124641,'close', '012220152100'); 
INSERT INTO Rental VALUES (3,124641,'open', '021220152100'); 
INSERT INTO Rental VALUES (6,149220,'open', '021220152100'); 
INSERT INTO Rental VALUES (7,197334,'open', '121220142100'); 
INSERT INTO Rental VALUES (8,227041,'open', '122320142100'); 