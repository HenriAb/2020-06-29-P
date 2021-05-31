package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Map<Integer, Match> getVertici(Month mese, Map<Integer, Match> idMap){
		
		final String sql = "SELECT m.MatchID AS id, m.TeamHomeID AS th, m.TeamAwayID AS ta, m.TeamHomeFormation AS thf, m.TeamAwayFormation AS taf, m.ResultOfTeamHome AS roth, m.Date AS d "
				+ "FROM matches m "
				+ "WHERE MONTH(m.Date) = ?";
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, mese.getValue());
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(!idMap.containsKey(res.getInt("id"))) {
					Match match = new Match(res.getInt("id"), res.getInt("th"), res.getInt("ta"), res.getInt("thf"), 
							res.getInt("taf"),res.getInt("roth"), res.getTimestamp("d").toLocalDateTime(), null, null);//, res.getString("t1.Name"),res.getString("t2.Name"));
					idMap.put(res.getInt("id"), match);
				}
				
			}
			conn.close();
			return idMap;
			
		} catch (SQLException e) {
			throw new RuntimeException("Errore DB", e);
		}
		
	}
	
	public List<Adiacenza> getArchi(Month m, Integer min, Map<Integer, Match> idMap){
		final String sql = "SELECT m1.MatchID, m2.MatchID, COUNT(DISTINCT(a1.PlayerID)) AS peso "
				+ "FROM matches m1, matches m2, actions a1, actions a2 "
				+ "WHERE m1.MatchID = a1.MatchID AND m2.MatchID = a2.MatchID AND a1.TimePlayed >= ? AND a2.PlayerID >= ? "
				+ "AND m1.MatchID > m2.MatchID AND MONTH(m1.Date) = MONTH(m2.Date) AND MONTH(m1.Date) = ? AND a1.PlayerID = a2.PlayerID "
				+ "GROUP BY m1.MatchID, m2.MatchID "
				+ "HAVING peso > 0";
		
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, min);
			st.setInt(2, min);
			st.setInt(3, m.getValue());
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				if(idMap.containsKey(rs.getInt("m1.MatchID")) && idMap.containsKey(rs.getInt("m2.MatchID"))){
					Adiacenza a = new Adiacenza(idMap.get(rs.getInt("m2.MatchID")), idMap.get(rs.getInt("m2.MatchID")), rs.getInt("peso"));
					result.add(a);
				}
			}
			
			conn.close();
			return result;
			
		}catch(SQLException e) {
			throw new RuntimeException("Errore DB", e);
		}
	}
	
}
