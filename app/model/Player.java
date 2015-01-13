package model;

import play.mvc.WebSocket;

public class Player {
	
	public static enum gameStat {WON, ONGOING, LOST};

	private String name = null;
	private int playerID;
	private WebSocket<String> webSock;
	private WebSocket.In<String> in;
	private Match match;
	private String msg;
	private gameStat stat = gameStat.ONGOING;

	public gameStat getStat() {
		return stat;
	}

	public void setStat(gameStat stat) {
		this.stat = stat;
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public WebSocket.In<String> getIn() {
		return in;
	}

	public void setIn(WebSocket.In<String> in) {
		this.in = in;
	}

	public WebSocket.Out<String> getOut() {
		return out;
	}

	public void setOut(WebSocket.Out<String> out) {
		this.out = out;
	}

	private WebSocket.Out<String> out;

	public Player(int pid) {
		this.playerID = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int uniqueID) {
		this.playerID = uniqueID;
	}

	public WebSocket<String> getWebSock() {
		return webSock;
	}

	public void setWebSock(WebSocket<String> webSock) {
		this.webSock = webSock;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
