create table goals
    (
        goal_id SERIAL,
        member_id int,
        goal_description text,
        achieve_by_date date default current_date,
        start_weight int,
        goal_weight int,
        start_time int,
        goal_time int,
        is_achieved boolean default false,
        primary key (goal_id),
        foreign key (member_id) REFERENCES members
    )

CREATE TABLE Trainers(
    --trainer info
    trainer_id SERIAL,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    salary int default 3000,
    primary key (trainer_id)
);

-- administrative staff entity 
CREATE TABLE Administrative_Staff(
    admin_id SERIAL,
    first_name varchar(255),
    last_name varchar(255),
    -- foreign key referencing schedule_id in schedule entity 
    schedule_id int REFERENCES Schedule(schedule_id)
);

CREATE TABLE Equipment(

equipment_id SERIAL,
equipment_name text,
equipment_maintenance_date Date,
primary key (equipment_id)
);

create table member_booking
(
    member_id   integer not null references members(member_id),
    schedule_id   integer not null references schedule(schedule_id)
);

CREATE TABLE billing
(
    member_id int,
    amount int not null,
    schedule_id int, 
    bill_date date default current_date,
    foreign key (member_id) references members
);
CREATE TABLE Schedule(
    schedule_id SERIAL,
    trainer_id int,
    appointment_time time not null,
    appointment_date date not null,
    appointment_room int not null,
    joining_fee int default 10,
    max_members int default 1,
    primary key (schedule_id),
    foreign key (trainer_id) references trainers
);
create table members
    (
        --member info
        member_id SERIAL,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        -- billing info
        date_joined date default current_date,
        fee int default 100,
        -- health metrics
        member_weight int default 0,
        member_height int default 0,
        member_time int default 0,
        --
        track_exercise_routine text not null,
        primary key (member_id)
    );