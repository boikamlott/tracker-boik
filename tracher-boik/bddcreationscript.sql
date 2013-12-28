/****************** Script permettant de créer la base de donénes nécessaire à l'utilisation de TrackerBoik ***********************************/
DELETE  FROM  hand_board WHERE 1;
DELETE  FROM  action WHERE 1;
DELETE  FROM  hand_player WHERE 1;
DELETE  FROM  hand WHERE 1;
DELETE  FROM  player_session_stats WHERE 1;
DELETE  FROM  session WHERE 1;
DELETE  FROM  board WHERE 1;
DELETE  FROM  player WHERE 1;

drop table hand_board;
drop table action;
drop table hand_player;
drop table hand;
drop table player_session_stats;
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
	comment VARCHAR(256)
);

/* Represente les stats agregees par joueurs et par session */
CREATE TABLE player_session_stats (
	player_id VARCHAR(256)  REFERENCES player(player_id),
	benefit DOUBLE,
	af_general_br INTEGER,
	af_general_call INTEGER,
	hands INTEGER,
	hands_flop INTEGER,
	hands_turn INTEGER,
	hands_river INTEGER,
	hands_vpip INTEGER,
	hands_pfr INTEGER,
	hands_ats INTEGER,
	ats INTEGER,
	hands_fats_sb INTEGER,
	hands_fats_bb INTEGER,
	fats_sb INTEGER,
	fats_bb INTEGER,
	hands_limp INTEGER,
	hands_ltf INTEGER,
	hands_ltc INTEGER,
	hands_3bet INTEGER,
	three_bet INTEGER,
	hands_f3bet INTEGER,
	f3bet INTEGER,
	af_flop_br INTEGER,
	af_flop_c INTEGER,
	hands_cbet INTEGER,
	cbet INTEGER,
	hands_fcbet INTEGER,
	fcbet INTEGER,
	af_turn_br INTEGER,
	af_turn_c INTEGER,
	hands_tcbet INTEGER,
	tcbet INTEGER,
	hands_ftcbet INTEGER,
	ftcet INTEGER,
	af_river_br INTEGER,
	af_river_c INTEGER,
	wmtsdf INTEGER,
	wtsd INTEGER,
	wmtsd INTEGER,
	CONSTRAINT pk_plasyer_session_stats PRIMARY KEY (player_id)
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