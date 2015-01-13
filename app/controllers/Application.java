package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.JavaController;
import org.pac4j.play.java.RequiresAuthentication;

import model.Match;
import model.Player;
import model.Player.gameStat;
import model.SimpleChat;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Result;
import play.mvc.WebSocket;
import de.htwg.xiangqi.XiangqiGame;
import de.htwg.xiangqi.controller.IBoardManager;

public class Application extends JavaController {

	private static int boardColSize = 9;
	private static int boardRowSize = boardColSize + 1;

	private static Match lonelyMatch = null;

	private static List<Player> players = new ArrayList<Player>();
	private static int nextMatchId = 1;
	private static int nextPlayerId = 1;

	public static Result index() {
        CommonProfile profile = getUserProfile();
        Player nu = new Player(nextPlayerId);

		players.add(nu);
		response().setCookie("id", "" + nextPlayerId);
		nextPlayerId++;

        if(profile != null){
            nu.setName(profile.getEmail());
            return ok(views.html.welcome.render(getRedirectAction("Google2Client").getLocation()), nu.getName());
        }
		return ok(views.html.welcome.render(getRedirectAction("Google2Client").getLocation()), "");
	}
	
	public static Result playGame() {
		Match m;
		int cookieId = Integer.parseInt(request().cookie("id").value());
		if (lonelyMatch != null) {
			m = lonelyMatch;
			lonelyMatch = null;
		} else {
			m = new Match(nextMatchId++);
			lonelyMatch = m;
		}

		players.get(cookieId - 1).setMatch(m);
		m.addPlayer(players.get(cookieId - 1));
		return ok(views.html.index.render(transformStringToArrayList(m.getXg()
				.getTui().printBoard()), null, m.getBm().getPlayersTurn(), cookieId % 2));
	}

	// Websocket intrface for Observersocket
	public static WebSocket<String> getNewObserverSocket() {
		int cookieId = Integer.parseInt(request().cookie("id").value());
		final Player p = players.get(cookieId - 1);

		WebSocket<String> ws = new WebSocket<String>() {
			@Override
			public void onReady(WebSocket.In<String> in,
					WebSocket.Out<String> out) {
				p.setIn(in);
				p.setOut(out);
				SimpleChat.connections.add(out);

				in.onMessage(new Callback<String>() {
					public void invoke(String event) {
                        String player;
                        if(p.getName() != null){
                            player = "["+p.getName()+"]: ";
                        }else{
                            player = "[Player RED]: ";
                            if((p.getPlayerID()%2) == 0){
                                player = "[Player BLACK]: ";
                            }    
                        }
						
						p.getMatch().updateChat(player + event);
					}
				});

				in.onClose(new Callback0() {
					public void invoke() {
						p.getMatch().updateChat("Opponent closed Game");
					}
				});
			};
		};
		p.setWebSock(ws);
		return ws;
	};

	public static Result WUIupdate() {
		int cookieId = Integer.parseInt(request().cookie("id").value());
		Player p = players.get(cookieId - 1);
		XiangqiGame xg = p.getMatch().getXg();
		IBoardManager bm = xg.getBm();
		return ok(views.html.index.render(transformStringToArrayList(xg
				.getTui().printBoard()), p.getMsg(), bm.getPlayersTurn(), cookieId % 2));
	}
	
	public static Result WUIwon() {
		int cookieId = Integer.parseInt(request().cookie("id").value());
		Player p = players.get(cookieId - 1);
		XiangqiGame xg = p.getMatch().getXg();
		return ok(views.html.theEndYouWon.render(transformStringToArrayList(xg
				.getTui().printBoard())));
	}
	
	public static Result WUIlost() {
		int cookieId = Integer.parseInt(request().cookie("id").value());
		Player p = players.get(cookieId - 1);
		XiangqiGame xg = p.getMatch().getXg();
		return ok(views.html.theEndYouLost.render(transformStringToArrayList(xg
				.getTui().printBoard())));
	}

	// get the ws.js script
	public static Result wsJs() {
		return ok(views.js.ws.render());
	}

	public static Result input(String s) {
		int cookieId = Integer.parseInt(request().cookie("id").value());
		System.out.println("\n\ninput()OUT:\n" + cookieId + "\n\n");
		Player p = players.get(cookieId - 1);
		XiangqiGame xg = p.getMatch().getXg();
		IBoardManager bm = xg.getBm();
		int pID = cookieId % 2;
		int turn = bm.getPlayersTurn();

		if (pID == turn) {
			boolean checkmate = bm.inputMove(s);
			p.setMsg(bm.getMessage());
			if (checkmate) {
				p.setStat(gameStat.WON);
				if(p.getPlayerID() == p.getMatch().getP1().getPlayerID()){
					p.getMatch().getP2().setStat(gameStat.LOST);
				}else{
					p.getMatch().getP1().setStat(gameStat.LOST);
				}
				p.setMsg(bm.winnerMessage());
				p.getMatch().update();
//				return ok(views.html.theEndYouWon.render(transformStringToArrayList(xg
//						.getTui().printBoard())));
				
			}
			
		} else {
			p.setMsg("Please wait! Opponent's turn...");
			
		}
		
		return ok(views.html.index.render(transformStringToArrayList(xg
					.getTui().printBoard()), p.getMsg(),
					turn, pID));
	}

	/*
	 * Addittional Logic for converting Strings to Lists - needed so we dont
	 * have to screw around in scala
	 */
	public static ArrayList<ArrayList<String>> transformStringToArrayList(
			String s) {
		ArrayList<ArrayList<String>> returnVal = new ArrayList<ArrayList<String>>(
				boardRowSize);

		String rowRegex = "(?m)^[0-9] .*$";/*
											 * ^ Stringanfang $ Stringende (?m)
											 * multiline
											 */
		String colRegex = "[A-Z ]{2}";/* {i} i-times */
		Pattern rowPattern = Pattern.compile(rowRegex);
		Pattern colPattern = Pattern.compile(colRegex);

		Matcher rowMatcher = rowPattern.matcher(s);
		while (rowMatcher.find()) {
			ArrayList<String> tmpList = new ArrayList<String>();
			Matcher colMatcher = colPattern.matcher(rowMatcher.group());
			while (colMatcher.find()) {
				tmpList.add(colMatcher.group());
			}
			returnVal.add(tmpList);
		}
		return returnVal;
	}
	
	
	
	public static Result Rule(String piece){
		
		switch(piece){
		case "general":
			return ok(views.html.rules_general.render());
		case "advisor":
			return ok(views.html.rules_advisor.render());
		case "elephant":
			return ok(views.html.rules_elephant.render());
		case "horse":
			return ok(views.html.rules_horse.render());
		case "chariot":
			return ok(views.html.rules_chariot.render());
		case "cannon":
			return ok(views.html.rules_cannon.render());
		case "soldier":			
			return ok(views.html.rules_soldier.render());
		
		default:
			return ok(views.html.rules_general.render());
		}
		
	}
}
