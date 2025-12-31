-- Insert into SpaceStation
INSERT INTO SpaceStation (station_id, name, launch_date, orbit) VALUES
(1, 'International Space Station', '1998-11-20', 'Low Earth Orbit'),
(2, 'Tiangong', '2021-04-29', 'Low Earth Orbit'),
(3, 'Hubble Space Telescope', '1990-04-24', 'Low Earth Orbit')
ON CONFLICT (station_id) DO NOTHING;

-- Insert into Project
INSERT INTO Project (project_id, name, status, start_date, end_date, budget, space_station_id) VALUES
(1, 'Mars Habitat Construction', 'ACTIVE', '2024-01-10', '2025-12-31', 2000000000, 1),
(2, 'Astrophysics Research Mission', 'COMPLETED', '2020-06-15', '2023-01-10', 500000000, 3),
(3, 'Satellite Deployment', 'PLANNED', '2026-02-15', '2025-06-30', 800000000, 2),
(4, 'Lunar Base Development', 'OVERDUE', '2021-05-01', '2023-07-31', 1500000000, 2)
ON CONFLICT (project_id) DO NOTHING;

-- Insert into Equipment
INSERT INTO Equipment (equipment_id, type, description, status, budget) VALUES
(1, 'Rover', 'Mars surface exploration rover', 'ACTIVE', 5000000),
(2, 'Telescope', 'Space observation telescope', 'INACTIVE', 20000000),
(3, 'Satellite Antenna', 'High frequency communication antenna', 'ACTIVE', 1000000),
(4, 'Airlock', 'Space station entry and exit point', 'INACTIVE', 300000)
ON CONFLICT (equipment_id) DO NOTHING;

-- Insert into Project_Equipment
INSERT INTO Project_Equipment (id, project_id, equipment_id) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 3)
ON CONFLICT (id) DO NOTHING;

-- Insert into Resource
INSERT INTO Resource (resource_id, type, quantity, unit, project_id) VALUES
(1, 'Solar Panels', 50, 'Units', 1),
(2, 'Fuel', 10000, 'Liters', 3),
(3, 'Research Kits', 200, 'Units', 2)
ON CONFLICT (resource_id) DO NOTHING;

-- Insert into System
INSERT INTO System (system_id, type, description, status, project_id) VALUES
(1, 'Communication', 'Satellite communication system', 'ACTIVE', 3),
(2, 'Life Support', 'Mars habitat life support system', 'ACTIVE', 1),
(3, 'Data Processing', 'Astrophysics data analysis system', 'INACTIVE', 2)
ON CONFLICT (system_id) DO NOTHING;

-- Insert into Module
INSERT INTO Module (module_id, type, description, status, project_id) VALUES
(1, 'Habitat Module', 'Living quarters for Mars mission', 'ACTIVE', 1),
(2, 'Laboratory Module', 'Space station research laboratory', 'INACTIVE', 2),
(3, 'Power Module', 'Energy generation module', 'ACTIVE', 3)
ON CONFLICT (module_id) DO NOTHING;

-- Insert into Document
INSERT INTO Document (document_id, name, type, version, modification_date, project_id) VALUES
(1, 'Mars Habitat Plan', 'Blueprint', '1.2', '2023-05-12', 1),
(2, 'Astrophysics Research Report', 'Study', '3.1', '2022-11-20', 2),
(3, 'Satellite Design', 'Blueprint', '2.0', '2023-08-18', 3)
ON CONFLICT (document_id) DO NOTHING;

-- Insert into Task
INSERT INTO Task (task_id, name, description, status, deadline, project_id) VALUES
(1, 'Assemble Rover', 'Assemble the Mars surface exploration rover', 'ACTIVE', '2025-11-17', 1),
(2, 'Data Collection', 'Collect astrophysics research data', 'COMPLETED', '2022-12-01', 2),
(3, 'Launch Preparation', 'Prepare satellite for launch', 'OVERDUE', '2023-12-31', 3),
(4, 'Lunar Base Design', 'Design lunar base for upcoming mission', 'OPEN', '2025-03-15', 4)
ON CONFLICT (task_id) DO NOTHING;

-- Insert into Experiment
INSERT INTO Experiment (experiment_id, name, description, status, deadline, project_id) VALUES
(1, 'Plant Growth Experiment', 'Study plant growth in Mars-like conditions', 'OPEN', '2025-10-30', 1),
(2, 'Cosmic Ray Analysis', 'Analyze cosmic rays using space telescope data', 'COMPLETED', '2023-01-05', 2),
(3, 'Satellite Trajectory', 'Experiment to determine optimal satellite trajectory', 'ACTIVE', '2025-02-20', 3),
(4, 'Zero-G Manufacturing', 'Test manufacturing techniques in zero gravity', 'OVERDUE', '2023-09-01', 2)
ON CONFLICT (experiment_id) DO NOTHING;

-- Insert into Actor
--INSERT INTO Actor (actor_id, role, contact_information, username, password) VALUES
--(1, 'SYSADMIN', 'admin@example.com', 'admin', '123456'),
--(2, 'MANAGER', 'manager@example.com', 'manager', '123456'),
--(3, 'SCIENTIST', 'scientist@example.com', 'scientist', '123456'),
--(4, 'ENGINEER', 'engineer@example.com', 'engineer', '123456'),
--(5, 'LOGISTICIAN', 'logist@example.com', 'logist', '123456');

-- Insert into Actor_Experiment
INSERT INTO Actor_Experiment (id, actor_id, experiment_id) VALUES
(1, 1, 1),
(2, 4, 2),
(3, 2, 3)
ON CONFLICT (id) DO NOTHING;

-- Insert into Actor_Task
INSERT INTO Actor_Task (id, actor_id, task_id) VALUES
(1, 2, 1),
(2, 3, 2),
(3, 5, 3)
ON CONFLICT (id) DO NOTHING;