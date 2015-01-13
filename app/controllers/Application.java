package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Match;
import model.Player;
import model.Player.gameStat;
import model.SimpleChat;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.JavaController;

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
        Player nu = null;
        if(profile != null){
        	nu = players.get(Integer.parseInt(request().cookie("id").value())-1);
		    
            nu.setName(profile.getEmail());
            return ok(views.html.welcome.render(getRedirectAction("Google2Client").getLocation(), nu.getName()));
        }else{
            nu = new Player(nextPlayerId);
            System.out.println("new player number: "+nextPlayerId);
    		players.add(nu);
	    	response().setCookie("id", "" + nextPlayerId);
		    nextPlayerId++;
        }
		return ok(views.html.welcome.render(getRedirectAction("Google2Client").getLocation(), ""));
	}
	
	public static synchronized Result playGame() {
		Match m;
        String tmpstr;
		int cookieId = Integer.parseInt(request().cookie("id").value());
        Player player = players.get(cookieId -1);

		if (lonelyMatch != null) {
			m = lonelyMatch;
            response().setCookie("turn", "black");
            tmpstr = "black";
			lonelyMatch = null;
		} else {
			m = new Match(nextMatchId++);
            response().setCookie("turn", "red");
            tmpstr = "red";
			lonelyMatch = m;
		}
        player.setName("Player " + tmpstr.toUpperCase());
		player.setMatch(m);
		m.addPlayer(player);
		return ok(views.html.index.render(transformStringToArrayList(m.getXg()
				.getTui().printBoard()), null, m.getBm().getPlayersTurn(), tmpstr));
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
						p.getMatch().updateChat(p, event);
					}
				});

				in.onClose(new Callback0() {
					public void invoke() {
						p.getMatch().updateChat(p, "Opponent closed Game");
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
				.getTui().printBoard()), p.getMsg(), bm.getPlayersTurn(), request().cookie("turn").value()));
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
		String color = request().cookie("turn").value();
		int turn = bm.getPlayersTurn();

		if (((color.equals("red"))&&(turn == 1))||((color.equals("black"))&&(turn == 0))) {
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
					turn, request().cookie("turn").value()));
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
