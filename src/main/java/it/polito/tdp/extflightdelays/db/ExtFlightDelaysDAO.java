package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Flight;
import it.polito.tdp.extflightdelays.model.Rotta;

public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public void loadAllAirports(Map<Integer, Airport> idMap) { //idMap di tutti gli aeroporti
		String sql = "SELECT * FROM airports";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				
				idMap.put(airport.getId(), airport);
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public Set<Airport> getVertici(Map<Integer, Airport> idMap, Double distanza){
		String sql = "select `ORIGIN_AIRPORT_ID`, `DESTINATION_AIRPORT_ID` " + 
				"from flights " + 
				"group by `ORIGIN_AIRPORT_ID`, `DESTINATION_AIRPORT_ID` " + 
				"having avg(distance) >=? ";
		
		Set<Airport> vertici = new HashSet<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, distanza);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport a1 = idMap.get(rs.getInt("ORIGIN_AIRPORT_ID"));
				Airport a2 = idMap.get(rs.getInt("DESTINATION_AIRPORT_ID"));
				
				if(a1!=null && a2!=null) {
					vertici.add(a1);
					vertici.add(a2);
				}
				
			}

			conn.close();
			return vertici;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	
	}
	
	/**
	 *  non metto la condizione origine<destinazione, perchè può darsi che ci sia solo un volo 80 => 2 e siccome origin > destinazione 
	 *  non verrebbe contato!
	 *
	 */
	public List<Rotta> getArchi(Map<Integer, Airport> idMap){
		String sql = "select `ORIGIN_AIRPORT_ID`, `DESTINATION_AIRPORT_ID`, avg(distance) as media " + 
				"from flights " + 
				"group by `ORIGIN_AIRPORT_ID`, `DESTINATION_AIRPORT_ID` ";
		List<Rotta> archi = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
		
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport a1 = idMap.get(rs.getInt("ORIGIN_AIRPORT_ID"));
				Airport a2 = idMap.get(rs.getInt("DESTINATION_AIRPORT_ID"));
				
				if(a1!=null && a2!=null) {
					archi.add(new Rotta(a1,a2, rs.getDouble("media")));
				}
				
			}

			conn.close();
			return archi;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
}

