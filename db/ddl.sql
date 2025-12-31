CREATE TABLE SpaceStation (
    station_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    launch_date DATE,
    orbit VARCHAR(255)
);

CREATE TABLE Project (
    project_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    status VARCHAR(20) CHECK (status IN ('PLANNED', 'ACTIVE', 'COMPLETED', 'OVERDUE')),
    start_date DATE,
    end_date DATE,
    budget BIGINT,
    space_station_id INT  NOT NULL,
    FOREIGN KEY (space_station_id) REFERENCES SpaceStation(station_id)
);

CREATE TABLE Equipment (
    equipment_id SERIAL PRIMARY KEY,
    type VARCHAR(20),
    description VARCHAR(255) UNIQUE,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'INACTIVE')),
    budget BIGINT
);

CREATE TABLE Project_Equipment (
    id SERIAL PRIMARY KEY,
    project_id INT,
    equipment_id INT,
    FOREIGN KEY (project_id) REFERENCES Project(project_id),
    FOREIGN KEY (equipment_id) REFERENCES Equipment(equipment_id)
);

CREATE TABLE Resource (
    resource_id SERIAL PRIMARY KEY,
    type VARCHAR(20),
    quantity BIGINT,
    unit VARCHAR(50),
    project_id INT,
    FOREIGN KEY (project_id) REFERENCES Project(project_id)
);

CREATE TABLE System (
    system_id SERIAL PRIMARY KEY,
    type VARCHAR(20),
    description VARCHAR(255) UNIQUE,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'INACTIVE')),
    project_id INT,
    FOREIGN KEY (project_id) REFERENCES Project(project_id)
);

CREATE TABLE Module (
    module_id SERIAL PRIMARY KEY,
    type VARCHAR(20),
    description VARCHAR(255) UNIQUE,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'INACTIVE')),
    project_id INT,
    FOREIGN KEY (project_id) REFERENCES Project(project_id)
);

CREATE TABLE Document (
    document_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    type VARCHAR(20),
    version VARCHAR(10),
    modification_date DATE,
    project_id INT,
    FOREIGN KEY (project_id) REFERENCES Project(project_id)
);

CREATE TABLE Task (
    task_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    description TEXT,
    status VARCHAR(20) CHECK (status IN ('OPEN', 'ACTIVE', 'COMPLETED', 'OVERDUE')),
    deadline DATE,
    project_id INT,
    FOREIGN KEY (project_id) REFERENCES Project(project_id)
);

CREATE TABLE Experiment (
    experiment_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    description TEXT,
    status VARCHAR(20) CHECK (status IN ('OPEN', 'ACTIVE', 'COMPLETED', 'OVERDUE')),
    deadline DATE,
    project_id INT,
    FOREIGN KEY (project_id) REFERENCES Project(project_id)
);

CREATE TABLE Actor (
    actor_id SERIAL PRIMARY KEY,
    role VARCHAR(50) NOT NULL CHECK (role IN ('MANAGER', 'ENGINEER', 'LOGISTICIAN', 'SCIENTIST', 'SYSADMIN')),
    contact_information VARCHAR(255),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE Actor_Experiment (
    id SERIAL PRIMARY KEY,
    actor_id INT,
    experiment_id INT,
    FOREIGN KEY (actor_id) REFERENCES Actor(actor_id),
    FOREIGN KEY (experiment_id) REFERENCES Experiment(experiment_id)
);

CREATE TABLE Actor_Task (
    id SERIAL PRIMARY KEY,
    actor_id INT,
    task_id INT,
    FOREIGN KEY (actor_id) REFERENCES Actor(actor_id),
    FOREIGN KEY (task_id) REFERENCES Task(task_id)
);