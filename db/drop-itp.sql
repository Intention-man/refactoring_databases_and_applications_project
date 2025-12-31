-- Удаление индексов
DROP INDEX IF EXISTS idx_project_name;
DROP INDEX IF EXISTS idx_task_status;
DROP INDEX IF EXISTS idx_resource_type;
DROP INDEX IF EXISTS idx_actor_role;
DROP INDEX IF EXISTS idx_space_station_name;
DROP INDEX IF EXISTS idx_project_space_station_id;
DROP INDEX IF EXISTS idx_project_status;
DROP INDEX IF EXISTS idx_equipment_type;
DROP INDEX IF EXISTS idx_resource_project_id;

-- Удаление процедур
DROP PROCEDURE IF EXISTS add_actor_to_task;
DROP PROCEDURE IF EXISTS add_actor_to_experiment;

-- Удаление триггеров
DROP TRIGGER IF EXISTS trg_check_project_overdue ON Project;
DROP TRIGGER IF EXISTS trg_check_task_overdue ON Task;
DROP TRIGGER IF EXISTS trg_check_experiment_overdue ON Experiment;

-- Удаление триггерных функций
DROP FUNCTION IF EXISTS check_project_overdue;
DROP FUNCTION IF EXISTS check_task_overdue;
DROP FUNCTION IF EXISTS check_experiment_overdue;

