/****************** Script permettant de créer la base de donénes nécessaire à l'utilisation de TrackerBoik ***********************************/
drop table hand_board;
drop table action;
drop table hand_player;
drop table hand;
drop table session;
drop table board;
drop table player;

/* Représente une session a une table */
CREATE TABLE session (
	session_id VARCHAR(256) PRIMARY KEY,
	file_associated_name VARCHAR(256) NOT NULL,
	session_kind VARCHAR(256),
	aggregated_data_calculated VARCHAR(10),
	CONSTRAINT agg_calculate_bool_enum CHECK (aggregated_data_calculated in ('y', 'n'))
);

/* Represente le board, le flop ne peut être nul sinon on aurait pas d'entrée */
CREATE TABLE board (
	board_id varchar(256) PRIMARY KEY,
	flop_1 varchar(2) NOT NULL,
	flop_2 varchar(2) NOT NULL,
	flop_3 varchar(2) NOT NULL,
	turn varchar(2),
	river varchar(2)
);

/* Represente une main disputée */
CREATE TABLE hand (
	hand_id VARCHAR(256) PRIMARY KEY,
	pot double NOT NULL,
	rake double NOT NULL,
	bb_value double NOT NULL,
	table_name VARCHAR(256),
	moment TIMESTAMP,
	bouton_seat_no INTEGER NOT NULL,
	nb_players INTEGER NOT NULL,
	session_id VARCHAR(256) REFERENCES session(session_id)
);

/* Represente le lien entre la main et le board */
CREATE TABLE hand_board (
	hand_id VARCHAR(256) PRIMARY KEY REFERENCES hand(hand_id),
	board_id VARCHAR(256) REFERENCES board(board_id)
);

/* Represente les joueurs */
CREATE TABLE player (
	player_id VARCHAR(256) PRIMARY KEY,
	comment VARCHAR(256),
	winrate DOUBLE,
	benefit DOUBLE,
	nb_hand_af_bet_raise INTEGER,
	nb_hand_af_call INTEGER,
	nb_hands INTEGER,
	nb_hands_vpip INTEGER,
	nb_hands_preflop_raise INTEGER,
	nb_hands_ats_possible INTEGER,
	nb_hands_ats INTEGER,
	nb_hands_fold_to_ats_sb_possible INTEGER,
	nb_hands_fold_to_ats_bb_possible INTEGER,
	nb_hands_fold_to_ats_sb INTEGER,
	nb_hands_fold_to_ats_bb INTEGER,
	nb_hands_3bet_possible INTEGER,
	nb_3bet INTEGER,
	nb_hands_fold_to_3bet_possible INTEGER,
	nb_hands_fold_to_3bet INTEGER,
	nb_cbet_possible INTEGER,
	nb_cbet INTEGER,
	nb_fold_to_cbet_possible INTEGER,
	nb_fold_to_cbet INTEGER,
	nb_second_barrel_possible INTEGER,
	nb_second_barrel INTEGER,
	nb_fold_to_second_barrel_possible INTEGER,
	nb_fold_to_second_barrel INTEGER
);	

/* Représente les mains auxquelles ont participés les joueurs */
CREATE TABLE hand_player (
	hand_id VARCHAR(256) REFERENCES hand(hand_id),
	player_id VARCHAR(256) REFERENCES player(player_id),
	card_1 VARCHAR(2),
	card_2 VARCHAR(2),
	position int NOT NULL,
	is_all_in VARCHAR(2) NOT NULL,
	result VARCHAR(50) NOT NULL,
	stack_before double NOT NULL,
	amount_win DOUBLE NOT NULL,
	CONSTRAINT all_in_bool_enum CHECK (is_all_in in ('y', 'n')),
	CONSTRAINT result_enum CHECK (result in ('no_bet', 'fold_preflop', 'fold_flop', 'fold_turn', 'fold_river', 'loose', 'win')),
	CONSTRAINT pk_hand_player PRIMARY KEY (hand_id, player_id)
);

/* Représente une action durant une main */
CREATE TABLE action (
	hand_id VARCHAR(256),
	player_id VARCHAR(256),
	action_number int NOT NULL,
	amount_bet double NOT NULL,
	kind VARCHAR(10) NOT NULL,
	moment VARCHAR(10) NOT NULL,
	CONSTRAINT pk_action PRIMARY KEY (hand_id, player_id, action_number),
	CONSTRAINT fk_hand_id_player_id_a FOREIGN KEY (hand_id, player_id) REFERENCES hand_player(hand_id, player_id),	
	CONSTRAINT kind_enum CHECK (kind in ('postSB', 'postBB', 'fold', 'check', 'call', 'raise', 'bet')),
	CONSTRAINT moment_enum CHECK (moment in ('preflop', 'flop', 'turn', 'river'))
);