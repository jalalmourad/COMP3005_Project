INSERT INTO members (first_name , last_name, member_weight, member_height, member_time,track_exercise_routine )
VALUES 
('Jason', 'Hobbs', 240, 76, 27,'Exercise 1'),
('Owen', 'Yang', 150, 178, 21,'Exercise 3' ),
('Bruce', 'Lee', 170, 175, 45, 'Exercise 2'),
('Cristiano', 'Ronaldo', 180, 185, 56,'Exercise 1'),
('Andres', 'Iniesta', 150, 166, 24, 'Exercise 5');

--Insertion to Trainer table
INSERT INTO Trainers(first_name,last_name)
VALUES
('Jalal', 'Mourad'),
('Alex', 'Smith'),
('Ronnie', 'Coleman'),
('David', 'Goggins');

--Insert to Schedule table

INSERT INTO Schedule( trainer_id, appointment_time, appointment_date, appointment_room)
VALUES
( 1, '12:45:00', '2024-09-01', 501),
( 2, '10:00:00', '2024-09-07', 503),
( 3, '09:15:00', '2024-10-01', 501);

--Insert to Administrative staff table
INSERT INTO administrative_Staff(first_name, last_name)
VALUES
('John', 'Williams'),
('Sam', 'Sulek'),
('Michael', 'Jordan'),
('Tommy', 'Hilfiger');

--Insert to goals table
INSERT INTO goals(member_id, goal_description, achieve_by_date, start_weight, goal_weight, start_time, goal_time, is_achieved)
VALUES
(1, 'lose weight', '2024-09-10', 240, 200, 15, 25, false),
(2, 'gain muscle', '2024-10-11', 150, 180, 30, 45, true),
(3, 'bench 405', '2024,08,26', 170, 405, 34, 56, false);

INSERT INTO Equipment(equipment_name,equipment_maintenance_date)
Values
('chest press machine','2023-10-09'),
('leg curl machine','2023-10-09'),
('treadmill','2024-11-09'),
('barbell','2022-08-02'),
('bench','2020-11-12');
-----------------------
