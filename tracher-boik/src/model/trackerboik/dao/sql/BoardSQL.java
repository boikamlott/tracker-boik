package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerBoard;
import model.trackerboik.businessobject.PokerCard;
import model.trackerboik.dao.BoardDAO;

import com.trackerboik.exception.TBException;

public class BoardSQL extends GeneralSQLDBOperations implements BoardDAO {
	public BoardSQL() throws TBException {
		super();
	}

	public static final String TABLE_NAME = "board";
	
	private static final String ATT_FLOP_1 = "flop_1",
								ATT_FLOP_2 = "flop_2",
								ATT_FLOP_3 = "flop_3",
								ATT_TURN = "turn",
								ATT_RIVER = "river";
	
	@Override
	public void createTable() throws TBException {
		
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
			   rq += GEN_ATT_BOARD_ID + " varchar(256) PRIMARY KEY,"; 	
			   rq += ATT_FLOP_1 + " varchar(2) NOT NULL,";	
			   rq += ATT_FLOP_2 + " varchar(2) NOT NULL,";
			   rq += ATT_FLOP_3 + " varchar(2) NOT NULL,";
			   rq += ATT_TURN + " varchar(2),";
			   rq += ATT_RIVER + " varchar(2))";
			   
		executeSQLUpdate(rq);		
	}

	@Override
	public void insertBoard(PokerBoard pb) throws TBException {
		try {
		
			psInsert.setString(1, pb.getID());			
			if(pb.getFlop() == null) { 
				for(int i = 2; i <= 4; i++) {
					psInsert.setString(i, "");
				}
			} else {
				for(int i = 0; i <= 2 ; i++) {
					psInsert.setString(i + 2, pb.getFlop().get(i).toString());
				}
			}
			
			psInsert.setString(5, pb.getTurn() == null ? "" : pb.getTurn().toString());
			psInsert.setString(6, pb.getRiver() == null ? "" : pb.getRiver().toString());
			
		
			if(psInsert.execute()) {
				throw new TBException("Unexpected result while trying to insert board " + pb.getID());
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to add board " + pb.getID() + " because: " + e.getMessage());
		}	
	}

	@Override
	protected String getInsertPreCompiledRequest() {
		return "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?)";
	}

	@Override
	protected String getExistenceTestPreCompiledRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_BOARD_ID + " = ?";
	}

	@Override
	protected String getAllElementsRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_BOARD_ID + " = ?";
	}

	@Override
	public void addBoardToHand(String boardID, Hand h) throws TBException {
		try {
			psQuery = createPreparedStatement(getAllElementsRequest());
			psQuery.setString(1, boardID);
			ResultSet rs = psQuery.executeQuery();
			if(!rs.next()) {
				throw new TBException("Impossible to find board '" + boardID + "' for hand '" + h.getId() + "'");
			}
			
			PokerBoard pb = new PokerBoard(boardID);
			List<PokerCard> flop = new ArrayList<PokerCard>();
			flop.add(PokerCard.readCard(rs.getString(ATT_FLOP_1)));
			flop.add(PokerCard.readCard(rs.getString(ATT_FLOP_2)));
			flop.add(PokerCard.readCard(rs.getString(ATT_FLOP_3)));
			pb.setFlop(flop);
			
			PokerCard turn = PokerCard.readCard(rs.getString(ATT_TURN));
			if(turn != null) {
				pb.setTurn(turn);
				PokerCard river = PokerCard.readCard(rs.getString(ATT_RIVER));
				if(river != null) { pb.setRiver(river); }
			}
			
			h.setBoard(pb);
		} catch (SQLException e) {
			throw new TBException("Error while performing add board to hand operation '" + e.getMessage() + "'" );
		}
		
	}

}
