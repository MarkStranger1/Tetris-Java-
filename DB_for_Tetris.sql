CREATE TABLE tetris_players (
    id SERIAL PRIMARY KEY,
    login VARCHAR(20) UNIQUE NOT NULL,
    password_tetris VARCHAR(20) NOT NULL,
    best_score INT DEFAULT 0
);

INSERT INTO tetris_players (login, password_tetris, best_score) VALUES
('player1', 'password', 10),
('gamer123', 'abc123', 7),
('tetrisPro', 'test', 11),
('blockKing', '123', 4),
('fastStacker', '12345678', 10);