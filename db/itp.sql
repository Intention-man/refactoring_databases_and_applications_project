CREATE INDEX idx_project_name ON Project(name);

CREATE INDEX idx_task_status ON Task(status);

CREATE INDEX idx_resource_type ON Resource(type);

CREATE INDEX idx_actor_role ON Actor(role);


CREATE INDEX idx_space_station_name ON SpaceStation(name);

CREATE INDEX idx_project_space_station_id ON Project(space_station_id);

CREATE INDEX idx_project_status ON Project(status);

CREATE INDEX idx_equipment_type ON Equipment(type);

CREATE INDEX idx_resource_project_id ON Resource(project_id);


CREATE OR REPLACE FUNCTION check_project_overdue()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status != 'COMPLETED' AND CURRENT_DATE > NEW.end_date THEN
        NEW.status := 'OVERDUE';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_project_overdue
AFTER INSERT OR UPDATE ON Project
FOR EACH ROW
EXECUTE FUNCTION check_project_overdue();



CREATE OR REPLACE FUNCTION check_task_overdue()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status != 'COMPLETED' AND CURRENT_DATE > NEW.deadline THEN
        NEW.status := 'OVERDUE';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_task_overdue
AFTER INSERT OR UPDATE ON Task
FOR EACH ROW
EXECUTE FUNCTION check_task_overdue();



CREATE OR REPLACE FUNCTION check_experiment_overdue()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status != 'COMPLETED' AND CURRENT_DATE > NEW.deadline THEN
        NEW.status := 'OVERDUE';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_experiment_overdue
AFTER INSERT OR UPDATE ON Experiment
FOR EACH ROW
EXECUTE FUNCTION check_experiment_overdue();


CREATE OR REPLACE PROCEDURE add_actor_to_task(
    p_actor_id INT,
    p_task_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO Actor_Task (actor_id, task_id)
    VALUES (p_actor_id, p_task_id);

    UPDATE Task
    SET status = 'ACTIVE'
    WHERE task_id = p_task_id AND status = 'OPEN';

    RAISE NOTICE 'Actor ID % assigned to Task ID % successfully.', p_actor_id, p_task_id;
END;
$$;


CREATE OR REPLACE PROCEDURE add_actor_to_experiment(
    p_actor_id INT,
    p_experiment_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO Actor_Experiment (actor_id, experiment_id)
    VALUES (p_actor_id, p_experiment_id);

    UPDATE Experiment
    SET status = 'ACTIVE'
    WHERE experiment_id = p_experiment_id AND status = 'OPEN';

    RAISE NOTICE 'Actor ID % assigned to Experiment ID % successfully.', p_actor_id, p_experiment_id;
END;
$$;
