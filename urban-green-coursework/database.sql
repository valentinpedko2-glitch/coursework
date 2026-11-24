-- Інформаційна система догляду за міськими зеленими насадженнями (варіант 19)
-- Створює БД з нуля: mysql -u root -p < database.sql
DROP DATABASE IF EXISTS urban_green;
CREATE DATABASE urban_green CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE urban_green;

-- 1. Дерево
CREATE TABLE tree (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    code                 VARCHAR(20)  NOT NULL UNIQUE,
    species              VARCHAR(100) NOT NULL,
    location             VARCHAR(150) NOT NULL,
    planting_year        INT          NOT NULL,
    pedestrians_per_hour INT          NOT NULL,
    CONSTRAINT chk_tree_pedestrians CHECK (pedestrians_per_hour >= 0)
) ENGINE = InnoDB;

-- 2. Огляд дерева (health_score: 100 - здорове, 0 - аварійне)
CREATE TABLE inspection (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    tree_id         INT          NOT NULL,
    inspection_date DATE         NOT NULL,
    health_score    INT          NOT NULL,
    defects         VARCHAR(255) NULL,
    CONSTRAINT chk_inspection_health CHECK (health_score BETWEEN 0 AND 100),
    CONSTRAINT fk_inspection_tree FOREIGN KEY (tree_id) REFERENCES tree (id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- 3. Заявка на роботи (життєвий цикл: CREATED -> VERIFIED -> PLANNED -> DONE)
CREATE TABLE work_request (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    tree_id        INT           NOT NULL,
    inspection_id  INT           NOT NULL,
    work_type      VARCHAR(20)   NOT NULL,
    description    VARCHAR(255)  NOT NULL,
    deadline_days  INT           NOT NULL,
    state          VARCHAR(20)   NOT NULL DEFAULT 'CREATED',
    f1             DECIMAL(5, 2) NOT NULL,
    f2             DECIMAL(5, 2) NOT NULL,
    f3             DECIMAL(5, 2) NOT NULL,
    priority_index DECIMAL(5, 2) NOT NULL,
    CONSTRAINT chk_request_state CHECK (state IN ('CREATED', 'VERIFIED', 'PLANNED', 'DONE')),
    CONSTRAINT chk_request_deadline CHECK (deadline_days >= 0),
    CONSTRAINT fk_request_tree FOREIGN KEY (tree_id) REFERENCES tree (id) ON DELETE CASCADE,
    CONSTRAINT fk_request_inspection FOREIGN KEY (inspection_id) REFERENCES inspection (id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- 4. Наряд (призначення бригади на заявку)
CREATE TABLE work_assignment (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    request_id   INT          NOT NULL,
    crew_name    VARCHAR(100) NOT NULL,
    planned_date DATE         NOT NULL,
    note         VARCHAR(255) NULL,
    CONSTRAINT fk_assignment_request FOREIGN KEY (request_id) REFERENCES work_request (id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- Демонстраційні дані
INSERT INTO tree (code, species, location, planting_year, pedestrians_per_hour) VALUES
    ('LP-001', 'Липа серцелиста',     'вул. Соборності, 12',  1987, 420),
    ('KS-014', 'Каштан кінський',     'Сквер Миру',           1975, 300),
    ('KL-102', 'Клен гостролистий',   'вул. Європейська, 45', 2005, 150),
    ('DB-007', 'Дуб звичайний',       'Київський парк',       1950,  80),
    ('TP-033', 'Тополя пірамідальна', 'вул. Зіньківська, 8',  1992, 260);

INSERT INTO inspection (tree_id, inspection_date, health_score, defects) VALUES
    (1, '2026-10-05', 40, 'Суховершинність, дупло у стовбурі'),
    (2, '2026-10-07', 65, 'Ураження листя мінуючою міллю'),
    (3, '2026-10-12', 85, 'Дрібні сухі гілки'),
    (4, '2026-10-15', 55, 'Тріщина основної гілки'),
    (5, '2026-10-20', 25, 'Нахил стовбура, загроза падіння');

-- Значення F1-F3 та I відповідають правилам нормування з пояснювальної записки.
INSERT INTO work_request (tree_id, inspection_id, work_type, description, deadline_days, state, f1, f2, f3, priority_index) VALUES
    (1, 1, 'PRUNING',   'Санітарна обрізка крони',        6, 'CREATED',  60.00, 84.00, 80.00, 73.40),
    (5, 5, 'REMOVAL',   'Видалення аварійного дерева',    2, 'VERIFIED', 75.00, 52.00, 93.33, 71.53),
    (2, 2, 'TREATMENT', 'Обробка від шкідників',         15, 'PLANNED',  35.00, 60.00, 50.00, 47.50);

INSERT INTO work_assignment (request_id, crew_name, planned_date, note) VALUES
    (3, 'Бригада №1', '2026-11-25', 'Обприскування крони'),
    (3, 'Бригада №2', '2026-11-26', 'Контроль результату обробки');
